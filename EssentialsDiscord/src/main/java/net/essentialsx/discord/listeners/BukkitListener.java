package net.essentialsx.discord.listeners;

import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.events.MuteStatusChangeEvent;
import net.essentialsx.api.v2.events.discord.DiscordMessageEvent;
import net.essentialsx.discord.EssentialsJDA;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        if (event.getValue()) {
            sendDiscordMessage(DiscordMessageEvent.MessageType.MUTE, event.getAffected().getBase().getName() + " has been muted.", false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        sendDiscordMessage(DiscordMessageEvent.MessageType.CHAT, event.getPlayer().getDisplayName() + ": " + event.getMessage(),
                event.getPlayer().hasPermission("essentials.discord.ping"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        sendDiscordMessage(DiscordMessageEvent.MessageType.JOIN, event.getPlayer().getName() + " has joined the server.", false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        sendDiscordMessage(DiscordMessageEvent.MessageType.LEAVE, event.getPlayer().getName() + " has left the server.", false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        sendDiscordMessage(DiscordMessageEvent.MessageType.DEATH, event.getEntity().getName() + " has died!", false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        sendDiscordMessage(DiscordMessageEvent.MessageType.KICK, event.getPlayer().getName() + " has been kicked.", false);
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
