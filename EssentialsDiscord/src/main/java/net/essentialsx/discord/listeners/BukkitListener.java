package net.essentialsx.discord.listeners;

import com.earth2me.essentials.Console;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.events.MuteStatusChangeEvent;
import net.essentialsx.api.v2.events.AsyncUserDataLoadEvent;
import net.essentialsx.api.v2.events.discord.DiscordMessageEvent;
import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.MessageFormat;

public class BukkitListener implements Listener {
    private final static String AVATAR_URL = "https://crafatar.com/avatars/{uuid}?overlay=true";
    private final EssentialsJDA jda;

    public BukkitListener(EssentialsJDA jda) {
        this.jda = jda;
    }

    /**
     * Processes messages from all other events.
     * This way it allows other plugins to modify route/message or just cancel it.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDiscordMessage(DiscordMessageEvent event) {
        jda.sendMessage(event, event.getMessage(), event.isAllowGroupMentions());
    }

    // Bukkit Events

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMute(MuteStatusChangeEvent event) {
        if (!event.getValue()) {
            sendDiscordMessage(DiscordMessageEvent.MessageType.MUTE, MessageUtil.formatMessage(jda.getSettings().getUnmuteFormat(),
                    event.getAffected().getName(), event.getAffected().getDisplayName()));
        } else if (event.getTimestamp().isPresent()) {
            final boolean console = event.getController() == null;
            final MessageFormat msg = event.getReason() == null ? jda.getSettings().getTempMuteFormat() : jda.getSettings().getTempMuteReasonFormat();
            sendDiscordMessage(DiscordMessageEvent.MessageType.MUTE, MessageUtil.formatMessage(msg,
                    event.getAffected().getName(), event.getAffected().getDisplayName(),
                    console ? Console.NAME : event.getController().getName(), console ? Console.DISPLAY_NAME : event.getController().getDisplayName(),
                    DateUtil.formatDateDiff(event.getTimestamp().get()), event.getReason()));
        } else {
            final boolean console = event.getController() == null;
            final MessageFormat msg = event.getReason() == null ? jda.getSettings().getPermMuteFormat() : jda.getSettings().getPermMuteReasonFormat();
            sendDiscordMessage(DiscordMessageEvent.MessageType.MUTE, MessageUtil.formatMessage(msg,
                    event.getAffected().getName(), event.getAffected().getDisplayName(),
                    console ? Console.NAME : event.getController().getName(), console ? Console.DISPLAY_NAME : event.getController().getDisplayName(),
                    event.getReason()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(jda.getPlugin(), () ->
                sendDiscordMessage(DiscordMessageEvent.MessageType.CHAT, MessageUtil.formatMessage(jda.getSettings().getMcToDiscordFormat(),
                        player.getName(), player.getDisplayName(),
                        player.hasPermission("essentials.discord.markdown") ? event.getMessage() : MessageUtil.sanitizeDiscordMarkdown(event.getMessage()),
                        player.getWorld().getName(), FormatUtil.stripEssentialsFormat(jda.getPlugin().getEss().getPermissionsHandler().getPrefix(player)),
                        FormatUtil.stripEssentialsFormat(jda.getPlugin().getEss().getPermissionsHandler().getSuffix(player))), player.hasPermission("essentials.discord.ping"),
                        jda.getSettings().isShowAvatar() ? AVATAR_URL.replace("{uuid}", player.getUniqueId().toString()) : null,
                        jda.getSettings().isShowName() ? player.getName() : null));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(AsyncUserDataLoadEvent event) {
        // Delay join to let nickname load
        if (event.getJoinMessage() != null) {
            sendDiscordMessage(DiscordMessageEvent.MessageType.JOIN, MessageUtil.formatMessage(jda.getSettings().getJoinFormat(),
                    event.getUser().getName(), event.getUser().getDisplayName(), event.getJoinMessage()), false,
                    jda.getSettings().isShowAvatar() ? AVATAR_URL.replace("{uuid}", event.getUser().getBase().getUniqueId().toString()) : null,
                    jda.getSettings().isShowName() ? event.getUser().getName() : null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        if (event.getQuitMessage() != null) {
            sendDiscordMessage(DiscordMessageEvent.MessageType.LEAVE, MessageUtil.formatMessage(jda.getSettings().getQuitFormat(),
                    event.getPlayer().getName(), event.getPlayer().getDisplayName(), event.getQuitMessage()), false,
                    jda.getSettings().isShowAvatar() ? AVATAR_URL.replace("{uuid}", event.getPlayer().getUniqueId().toString()) : null,
                    jda.getSettings().isShowName() ? event.getPlayer().getName() : null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        sendDiscordMessage(DiscordMessageEvent.MessageType.DEATH, MessageUtil.formatMessage(jda.getSettings().getDeathFormat(),
                event.getEntity().getName(), event.getEntity().getDisplayName(), event.getDeathMessage()), false,
                jda.getSettings().isShowAvatar() ? AVATAR_URL.replace("{uuid}", event.getEntity().getUniqueId().toString()) : null,
                jda.getSettings().isShowName() ? event.getEntity().getName() : null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        sendDiscordMessage(DiscordMessageEvent.MessageType.KICK, MessageUtil.formatMessage(jda.getSettings().getKickFormat(),
                event.getPlayer().getName(), event.getPlayer().getDisplayName(), event.getReason()));
    }

    private void sendDiscordMessage(final DiscordMessageEvent.MessageType messageType, final String message) {
        sendDiscordMessage(messageType, message, false, null, null);
    }

    private void sendDiscordMessage(final DiscordMessageEvent.MessageType messageType, final String message, final boolean allowPing, final String avatarUrl, final String name) {
        if (jda.getPlugin().getSettings().getMessageChannel(messageType.getKey()).equalsIgnoreCase("none")) {
            return;
        }

        final DiscordMessageEvent event = new DiscordMessageEvent(messageType, FormatUtil.stripFormat(message), allowPing, avatarUrl, name);
        if (Bukkit.getServer().isPrimaryThread()) {
            Bukkit.getPluginManager().callEvent(event);
        } else {
            Bukkit.getScheduler().runTask(jda.getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
        }
    }
}
