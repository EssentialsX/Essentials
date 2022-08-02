package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.utils.FormatUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.ess3.api.events.LocalChatSpyEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
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
    protected boolean isPapi;

    protected AbstractChatHandler(Essentials ess, EssentialsChat essChat) {
        this.ess = ess;
        this.essChat = essChat;
        this.server = ess.getServer();
        this.cache = new ChatProcessingCache();
        final Plugin papi = essChat.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        this.isPapi = papi != null && papi.isEnabled();
    }

    // The initial chat formatting logic, handled at LOWEST priority
    protected void handleChatFormat(AsyncPlayerChatEvent event) {
        if (isAborted(event)) {
            return;
        }

        final User user = ess.getUser(event.getPlayer());

        if (user == null) {
            event.setCancelled(true);
            return;
        }

        final ChatProcessingCache.IntermediateChat chat = new ChatProcessingCache.IntermediateChat(user, getChatType(user, event.getMessage()), event.getMessage());
        cache.setIntermediateChat(event.getPlayer(), chat);

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
        if (isPapi) {
            format = PlaceholderAPI.setPlaceholders(event.getPlayer(), format);
        }
        synchronized (format) {
            event.setFormat(format);
        }

        chat.setFormatResult(format);
        chat.setMessageResult(event.getMessage());
    }

    // Local chat recipients logic, handled at NORMAL level
    protected void handleChatRecipients(AsyncPlayerChatEvent event) {
        if (isAborted(event)) {
            return;
        }

        // This file should handle detection of the local chat features; if local chat is enabled, we need to handle it here
        long radius = ess.getSettings().getChatRadius();
        if (radius < 1) {
            return;
        }
        radius *= radius;

        final ChatProcessingCache.Chat chatStore = cache.getIntermediateOrElseProcessedChat(event.getPlayer());
        final User user = chatStore.getUser();

        if (event.getMessage().length() > 1) {
            if (chatStore.getType().isEmpty()) {
                if (!user.isAuthorized("essentials.chat.local")) {
                    user.sendMessage(tl("notAllowedToLocal"));
                    event.setCancelled(true);
                    return;
                }

                if (user.isToggleShout() && event.getMessage().length() > 1 && event.getMessage().charAt(0) == ess.getSettings().getChatShout()) {
                    event.setMessage(event.getMessage().substring(1));
                }

                event.getRecipients().removeIf(player -> !ess.getUser(player).isAuthorized("essentials.chat.receive.local"));
            } else {
                final String permission = "essentials.chat." + chatStore.getType();

                if (user.isAuthorized(permission)) {
                    // TODO: move this formatting over to handleChatFormat to avoid breaking signing
                    if (event.getMessage().charAt(0) == ess.getSettings().getChatShout() || (event.getMessage().charAt(0) == ess.getSettings().getChatQuestion() && ess.getSettings().isChatQuestionEnabled())) {
                        event.setMessage(event.getMessage().substring(1));
                    }
                    event.setFormat(tl(chatStore.getType() + "Format", event.getFormat()));
                    event.getRecipients().removeIf(player -> !ess.getUser(player).isAuthorized("essentials.chat.receive." + chatStore.getType()));
                    return;
                }

                user.sendMessage(tl("notAllowedTo" + chatStore.getType().substring(0, 1).toUpperCase(Locale.ENGLISH) + chatStore.getType().substring(1)));
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

        final String format = event.getFormat();
        event.setFormat(tl("chatTypeLocal").concat(event.getFormat()));

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
                    if (delta > radius) {
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

        final LocalChatSpyEvent spyEvent = new LocalChatSpyEvent(event.isAsynchronous(), event.getPlayer(), format, event.getMessage(), spyList);
        server.getPluginManager().callEvent(spyEvent);

        if (!spyEvent.isCancelled()) {
            for (final Player onlinePlayer : spyEvent.getRecipients()) {
                onlinePlayer.sendMessage(String.format(spyEvent.getFormat(), user.getDisplayName(), spyEvent.getMessage()));
            }
        }
    }

    // Finalising the intermediate stages of chat processing, handled at HIGHEST level during previews
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

    private class PluginListener implements Listener {
        void onPluginEnable(PluginEnableEvent event) {
            if (event.getPlugin().getName().equals("PlaceholderAPI")) {
                isPapi = true;
            }
        }

        void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin().getName().equals("PlaceholderAPI")) {
                isPapi = false;
            }
        }
    }

}
