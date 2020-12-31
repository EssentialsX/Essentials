package net.essentialsx.discord.listeners;

import com.earth2me.essentials.Console;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.events.MuteStatusChangeEvent;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.MessageFormat;

public class BukkitListener implements Listener {
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
        jda.sendMessage(event.getType(), event.getMessage(), event.isAllowGroupMentions());
    }

    // Bukkit Events

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMute(MuteStatusChangeEvent event) {
        if (!event.getValue()) {
            sendDiscordMessage(DiscordMessageEvent.MessageType.MUTE, MessageUtil.formatMessage(jda.getSettings().getUnmuteFormat(),
                    event.getAffected().getName(), event.getAffected().getDisplayName()), false);
        } else if (event.getTimestamp().isPresent()) {
            final boolean console = event.getController() == null;
            final MessageFormat msg = event.getReason() == null ? jda.getSettings().getTempMuteFormat() : jda.getSettings().getTempMuteReasonFormat();
            sendDiscordMessage(DiscordMessageEvent.MessageType.MUTE, MessageUtil.formatMessage(msg,
                    event.getAffected().getName(), event.getAffected().getDisplayName(),
                    console ? Console.NAME : event.getController().getName(), console ? Console.DISPLAY_NAME : event.getController().getDisplayName(),
                    DateUtil.formatDateDiff(event.getTimestamp().get()), event.getReason()), false);
        } else {
            final boolean console = event.getController() == null;
            final MessageFormat msg = event.getReason() == null ? jda.getSettings().getPermMuteFormat() : jda.getSettings().getPermMuteReasonFormat();
            sendDiscordMessage(DiscordMessageEvent.MessageType.MUTE, MessageUtil.formatMessage(msg,
                    event.getAffected().getName(), event.getAffected().getDisplayName(),
                    console ? Console.NAME : event.getController().getName(), console ? Console.DISPLAY_NAME : event.getController().getDisplayName(),
                    event.getReason()), false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(jda.getPlugin(), () ->
                sendDiscordMessage(DiscordMessageEvent.MessageType.CHAT, MessageUtil.formatMessage(jda.getSettings().getMcToDiscordFormat(),
                        player.getName(), player.getDisplayName(),
                        player.hasPermission("essentials.discord.markdown") ? event.getMessage() : MessageUtil.sanitizeDiscordMarkdown(event.getMessage()),
                        player.getWorld().getName(), jda.getPlugin().getEss().getPermissionsHandler().getPrefix(player),
                        jda.getPlugin().getEss().getPermissionsHandler().getSuffix(player)), player.hasPermission("essentials.discord.ping")));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        sendDiscordMessage(DiscordMessageEvent.MessageType.JOIN, MessageUtil.formatMessage(jda.getSettings().getJoinFormat(),
                event.getPlayer().getName(), event.getPlayer().getDisplayName(), event.getJoinMessage()), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        sendDiscordMessage(DiscordMessageEvent.MessageType.LEAVE, MessageUtil.formatMessage(jda.getSettings().getQuitFormat(),
                event.getPlayer().getName(), event.getPlayer().getDisplayName(), event.getQuitMessage()), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        sendDiscordMessage(DiscordMessageEvent.MessageType.DEATH, MessageUtil.formatMessage(jda.getSettings().getDeathFormat(),
                event.getEntity().getName(), event.getEntity().getDisplayName(), event.getDeathMessage()), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        sendDiscordMessage(DiscordMessageEvent.MessageType.KICK, MessageUtil.formatMessage(jda.getSettings().getKickFormat(),
                event.getPlayer().getName(), event.getPlayer().getDisplayName(), event.getReason()), false);
    }

    private void sendDiscordMessage(DiscordMessageEvent.MessageType messageType, String message, boolean allowPing) {
        if (jda.getPlugin().getSettings().getMessageChannel(messageType.getKey()).equalsIgnoreCase("none")) {
            return;
        }

        final DiscordMessageEvent event = new DiscordMessageEvent(messageType, FormatUtil.stripFormat(message), allowPing);
        if (Bukkit.getServer().isPrimaryThread()) {
            Bukkit.getPluginManager().callEvent(event);
        } else {
            Bukkit.getScheduler().runTask(jda.getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
        }
    }
}
