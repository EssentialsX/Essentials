package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.events.LocalChatSpyEvent;
import net.essentialsx.api.v2.ChatType;
import net.essentialsx.api.v2.events.chat.ChatEvent;
import net.essentialsx.api.v2.events.chat.GlobalChatEvent;
import net.essentialsx.api.v2.events.chat.LocalChatEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

public abstract class AbstractChatHandler {

    protected final Essentials ess;
    protected final EssentialsChat essChat;
    protected final Server server;
    protected final ChatProcessingCache cache;

    protected AbstractChatHandler(Essentials ess, EssentialsChat essChat) {
        this.ess = ess;
        this.essChat = essChat;
        this.server = ess.getServer();
        this.cache = new ChatProcessingCache();
    }

    /**
     * Apply chat formatting from config and from translations according to chat type.
     * <p>
     * Handled at {@link org.bukkit.event.EventPriority#LOWEST} on both preview and chat events.
     */
    protected void handleChatFormat(AsyncPlayerChatEvent event) {
        if (isAborted(event)) {
            return;
        }

        final User user = ess.getUser(event.getPlayer());

        if (user == null) {
            event.setCancelled(true);
            return;
        }

        // Ensure we're getting the latest display name
        user.setDisplayNick();

        // Reuse cached IntermediateChat if available
        ChatProcessingCache.ProcessedChat chat = cache.getProcessedChat(event.getPlayer());
        if (chat == null) {
            chat = new ChatProcessingCache.ProcessedChat(user, getChatType(user, event.getMessage()), event.getMessage());
            cache.setProcessedChat(event.getPlayer(), chat);
        }

        final long configRadius = ess.getSettings().getChatRadius();
        chat.setRadius(Math.max(configRadius, 0));

        // This listener should apply the general chat formatting only...then return control back the event handler
        event.setMessage(FormatUtil.formatMessage(user, "essentials.chat", event.getMessage()));

        if (ChatColor.stripColor(event.getMessage()).length() == 0) {
            event.setCancelled(true);
            return;
        }

        final String group = user.getGroup();
        final String world = user.getWorld().getName();
        final String username = user.getName();
        final String nickname = user.getFormattedNickname();

        final Player player = user.getBase();
        final String prefix = FormatUtil.replaceFormat(ess.getPermissionsHandler().getPrefix(player));
        final String suffix = FormatUtil.replaceFormat(ess.getPermissionsHandler().getSuffix(player));
        final Team team = player.getScoreboard().getPlayerTeam(player);

        String format = ess.getSettings().getChatFormat(group);
        format = format.replace("{0}", group);
        format = format.replace("{1}", ess.getSettings().getWorldAlias(world));
        format = format.replace("{2}", world.substring(0, 1).toUpperCase(Locale.ENGLISH));
        format = format.replace("{3}", team == null ? "" : team.getPrefix());
        format = format.replace("{4}", team == null ? "" : team.getSuffix());
        format = format.replace("{5}", team == null ? "" : team.getDisplayName());
        format = format.replace("{6}", prefix);
        format = format.replace("{7}", suffix);
        format = format.replace("{8}", username);
        format = format.replace("{9}", nickname == null ? username : nickname);

        // Local, shout and question chat types are only enabled when there's a valid radius
        if (chat.getRadius() > 0 && event.getMessage().length() > 0) {
            if (event.getMessage().length() > 1 && ((chat.getType() == ChatType.SHOUT && event.getMessage().charAt(0) == ess.getSettings().getChatShout()) || (chat.getType() == ChatType.QUESTION && event.getMessage().charAt(0) == ess.getSettings().getChatQuestion()))) {
                event.setMessage(event.getMessage().substring(1));
            }

            if (chat.getType() == ChatType.UNKNOWN) {
                format = AdventureUtil.miniToLegacy(tlLiteral("chatTypeLocal")).concat(format);
            } else {
                format = AdventureUtil.miniToLegacy(tlLiteral(chat.getType().key() + "Format", format));
            }
        }

        // Long live pointless synchronized blocks!
        synchronized (format) {
            event.setFormat(format);
        }
    }

    /**
     * Handle the recipient filtering and permissions checks for local chat, if enabled.
     * <p>
     * Runs at {@link org.bukkit.event.EventPriority#NORMAL} priority on submitted chat events only.
     */
    protected void handleChatRecipients(AsyncPlayerChatEvent event) {
        if (isAborted(event)) {
            return;
        }

        final ChatProcessingCache.Chat chat = cache.getProcessedChat(event.getPlayer());

        // If local chat is enabled, handle the recipients here; else we have nothing to do
        if (chat.getRadius() < 1) {
            return;
        }
        final long radiusSquared = chat.getRadius() * chat.getRadius();

        final User user = chat.getUser();

        if (event.getMessage().length() > 0) {
            if (chat.getType() == ChatType.UNKNOWN) {
                if (!user.isAuthorized("essentials.chat.local")) {
                    user.sendTl("notAllowedToLocal");
                    event.setCancelled(true);
                    return;
                }

                event.getRecipients().removeIf(player -> !ess.getUser(player).isAuthorized("essentials.chat.receive.local"));
            } else {
                final String permission = "essentials.chat." + chat.getType().key();

                if (user.isAuthorized(permission)) {
                    event.getRecipients().removeIf(player -> !ess.getUser(player).isAuthorized("essentials.chat.receive." + chat.getType().key()));

                    callChatEvent(event, chat.getType(), null);
                } else {
                    final String chatType = chat.getType().name();
                    user.sendTl("notAllowedTo" + chatType.charAt(0) + chatType.substring(1).toLowerCase(Locale.ENGLISH));
                    event.setCancelled(true);
                }
                return;
            }
        }

        final Location loc = user.getLocation();
        final World world = loc.getWorld();

        final Set<Player> outList = event.getRecipients();
        final Set<Player> spyList = new HashSet<>();

        try {
            outList.add(event.getPlayer());
        } catch (final UnsupportedOperationException ex) {
            if (ess.getSettings().isDebug()) {
                essChat.getLogger().log(Level.INFO, "Plugin triggered custom chat event, local chat handling aborted.", ex);
            }
            return;
        }

        final Iterator<Player> it = outList.iterator();
        while (it.hasNext()) {
            final Player onlinePlayer = it.next();
            final User onlineUser = ess.getUser(onlinePlayer);
            if (!onlineUser.equals(user)) {
                boolean abort = false;
                final Location playerLoc = onlineUser.getLocation();
                if (playerLoc.getWorld() != world) {
                    abort = true;
                } else {
                    final double delta = playerLoc.distanceSquared(loc);
                    if (delta > radiusSquared) {
                        abort = true;
                    }
                }
                if (abort) {
                    if (onlineUser.isAuthorized("essentials.chat.spy")) {
                        spyList.add(onlinePlayer);
                    }
                    it.remove();
                }
            }
        }

        callChatEvent(event, ChatType.LOCAL, chat.getRadius());

        if (event.isCancelled()) {
            return;
        }

        if (outList.size() < 2) {
            user.sendTl("localNoOne");
        }

        // Strip local chat prefix to preserve API behaviour
        final String localPrefix = AdventureUtil.miniToLegacy(tlLiteral("chatTypeLocal"));
        String baseFormat = AdventureUtil.legacyToMini(event.getFormat());
        if (event.getFormat().startsWith(localPrefix)) {
            baseFormat = baseFormat.substring(localPrefix.length());
        }

        final LocalChatSpyEvent spyEvent = new LocalChatSpyEvent(event.isAsynchronous(), event.getPlayer(), baseFormat, event.getMessage(), spyList);
        server.getPluginManager().callEvent(spyEvent);

        if (!spyEvent.isCancelled()) {
            final String legacyString = AdventureUtil.miniToLegacy(String.format(spyEvent.getFormat(), AdventureUtil.legacyToMini(user.getDisplayName()), AdventureUtil.escapeTags(spyEvent.getMessage())));

            for (final Player onlinePlayer : spyEvent.getRecipients()) {
                onlinePlayer.sendMessage(legacyString);
            }
        }
    }

    /**
     * Re-create type-based chat event from the base chat event, call it and mirror changes back to the base chat event.
     * @param event Event based on which a type-based event will be created, and to which changes will be applied.
     * @param chatType Chat type which determines which event will be created and called.
     * @param radius If chat is a local chat, this is a non-squared radius used to calculate recipients, otherwise {@code null}.
     */
    protected void callChatEvent(final AsyncPlayerChatEvent event, final ChatType chatType, final Long radius) {
        final ChatEvent chatEvent;

        if (chatType == ChatType.LOCAL) {
            chatEvent = new LocalChatEvent(event.isAsynchronous(), event.getPlayer(), event.getFormat(), event.getMessage(), event.getRecipients(), radius);
        } else {
            chatEvent = new GlobalChatEvent(event.isAsynchronous(), chatType, event.getPlayer(), event.getFormat(), event.getMessage(), event.getRecipients());
        }

        server.getPluginManager().callEvent(chatEvent);

        event.setFormat(chatEvent.getFormat());
        event.setMessage(chatEvent.getMessage());
        event.setCancelled(chatEvent.isCancelled());
    }

    /**
     * Finalise the formatting stage of chat processing.
     * <p>
     * Handled at {@link org.bukkit.event.EventPriority#HIGHEST} during previews, and immediately after
     * {@link #handleChatFormat(AsyncPlayerChatEvent)} when previews are not available.
     */
    protected void handleChatPostFormat(AsyncPlayerChatEvent event) {
        if (isAborted(event)) {
            cache.clearProcessedChat(event.getPlayer());
        }
    }

    /**
     * Run costs for chat and clean up the cached {@link com.earth2me.essentials.chat.processing.ChatProcessingCache.ProcessedChat}
     */
    protected void handleChatSubmit(AsyncPlayerChatEvent event) {
        if (isAborted(event)) {
            return;
        }

        // This file should handle charging the user for the action before returning control back
        charge(event, cache.getProcessedChat(event.getPlayer()));

        cache.clearProcessedChat(event.getPlayer());
    }

    boolean isAborted(final AsyncPlayerChatEvent event) {
        return event.isCancelled();
    }

    ChatType getChatType(final User user, final String message) {
        if (message.length() == 0) {
            //Ignore empty chat events generated by plugins
            return ChatType.UNKNOWN;
        }

        final char shoutPrefix = ess.getSettings().getChatShout();
        final char questionPrefix = ess.getSettings().getChatQuestion();

        final char prefix = message.charAt(0);
        final boolean singleChar = message.length() == 1;

        if (singleChar) {
            if (user.isToggleShout()) {
                return ChatType.SHOUT;
            }
            return ChatType.UNKNOWN;
        }

        if (prefix == questionPrefix && ess.getSettings().isChatQuestionEnabled()) {
            return ChatType.QUESTION;
        } else if (prefix == shoutPrefix || user.isToggleShout()) {
            return ChatType.SHOUT;
        } else {
            return ChatType.UNKNOWN;
        }
    }

    private void charge(final User user, final Trade charge) throws ChargeException {
        charge.charge(user);
    }

    boolean charge(final AsyncPlayerChatEvent event, final ChatProcessingCache.ProcessedChat chat) {
        try {
            charge(chat.getUser(), chat.getCharge());
        } catch (final ChargeException e) {
            ess.showError(chat.getUser().getSource(), e, "\\ chat " + chat.getLongType());
            event.setCancelled(true);
            return false;
        }
        return true;
    }

    protected interface ChatListener extends Listener {
        @SuppressWarnings("unused")
        void onPlayerChat(AsyncPlayerChatEvent event);
    }

}
