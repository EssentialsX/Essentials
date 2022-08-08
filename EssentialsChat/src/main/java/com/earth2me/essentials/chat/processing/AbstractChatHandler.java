package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.events.LocalChatSpyEvent;
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

import static com.earth2me.essentials.I18n.tl;

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

        // Reuse cached IntermediateChat if available
        ChatProcessingCache.IntermediateChat chat = cache.getIntermediateChat(event.getPlayer());
        if (chat == null) {
            chat = new ChatProcessingCache.IntermediateChat(user, getChatType(user, event.getMessage()), event.getMessage());
            cache.setIntermediateChat(event.getPlayer(), chat);
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
            if (chat.getType().isEmpty()) {
                if (user.isToggleShout() && event.getMessage().charAt(0) == ess.getSettings().getChatShout()) {
                    event.setMessage(event.getMessage().substring(1));
                }
                format = tl("chatTypeLocal").concat(format);
            } else {
                if (event.getMessage().charAt(0) == ess.getSettings().getChatShout() || (event.getMessage().charAt(0) == ess.getSettings().getChatQuestion() && ess.getSettings().isChatQuestionEnabled())) {
                    event.setMessage(event.getMessage().substring(1));
                }
                format = tl(chat.getType() + "Format", format);
            }
        }

        // Long live pointless synchronized blocks!
        synchronized (format) {
            event.setFormat(format);
        }

        chat.setFormatResult(event.getFormat());
        chat.setMessageResult(event.getMessage());
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

        final ChatProcessingCache.Chat chat = cache.getIntermediateOrElseProcessedChat(event.getPlayer());

        // If local chat is enabled, handle the recipients here; else we have nothing to do
        if (chat.getRadius() < 1) {
            return;
        }
        final long radiusSquared = chat.getRadius() * chat.getRadius();

        final User user = chat.getUser();

        if (event.getMessage().length() > 0) {
            if (chat.getType().isEmpty()) {
                if (!user.isAuthorized("essentials.chat.local")) {
                    user.sendMessage(tl("notAllowedToLocal"));
                    event.setCancelled(true);
                    return;
                }

                event.getRecipients().removeIf(player -> !ess.getUser(player).isAuthorized("essentials.chat.receive.local"));
            } else {
                final String permission = "essentials.chat." + chat.getType();

                if (user.isAuthorized(permission)) {
                    event.getRecipients().removeIf(player -> !ess.getUser(player).isAuthorized("essentials.chat.receive." + chat.getType()));
                    return;
                }

                user.sendMessage(tl("notAllowedTo" + chat.getType().substring(0, 1).toUpperCase(Locale.ENGLISH) + chat.getType().substring(1)));
                event.setCancelled(true);
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

        if (outList.size() < 2) {
            user.sendMessage(tl("localNoOne"));
        }

        // Strip local chat prefix to preserve API behaviour
        final String localPrefix = tl("chatTypeLocal");
        String baseFormat = event.getFormat();
        if (event.getFormat().startsWith(localPrefix)) {
            baseFormat = baseFormat.substring(localPrefix.length());
        }

        final LocalChatSpyEvent spyEvent = new LocalChatSpyEvent(event.isAsynchronous(), event.getPlayer(), baseFormat, event.getMessage(), spyList);
        server.getPluginManager().callEvent(spyEvent);

        if (!spyEvent.isCancelled()) {
            for (final Player onlinePlayer : spyEvent.getRecipients()) {
                onlinePlayer.sendMessage(String.format(spyEvent.getFormat(), user.getDisplayName(), spyEvent.getMessage()));
            }
        }
    }

    /**
     * Finalise the formatting stage of chat processing.
     * <p>
     * Handled at {@link org.bukkit.event.EventPriority#HIGHEST} during previews, and immediately after
     * {@link #handleChatFormat(AsyncPlayerChatEvent)} when previews are not available.
     */
    protected void handleChatPostFormat(AsyncPlayerChatEvent event) {
        final ChatProcessingCache.IntermediateChat intermediateChat = cache.clearIntermediateChat(event.getPlayer());
        if (isAborted(event) || intermediateChat == null) {
            return;
        }

        // in case of modifications by other plugins during the preview
        intermediateChat.setFormatResult(event.getFormat());
        intermediateChat.setMessageResult(event.getMessage());

        final ChatProcessingCache.ProcessedChat processed = new ChatProcessingCache.ProcessedChat(ess, intermediateChat);
        cache.setProcessedChat(event.getPlayer(), processed);
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

    String getChatType(final User user, final String message) {
        if (message.length() == 0) {
            //Ignore empty chat events generated by plugins
            return "";
        }

        final char prefix = message.charAt(0);
        if (prefix == ess.getSettings().getChatShout()) {
            if (user.isToggleShout()) {
                return "";
            }
            return message.length() > 1 ? "shout" : "";
        } else if (ess.getSettings().isChatQuestionEnabled() && prefix == ess.getSettings().getChatQuestion()) {
            return message.length() > 1 ? "question" : "";
        } else if (user.isToggleShout()) {
            return message.length() > 1 ? "shout" : "";
        } else {
            return "";
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
