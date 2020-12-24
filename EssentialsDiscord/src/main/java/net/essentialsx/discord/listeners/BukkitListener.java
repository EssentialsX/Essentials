package net.essentialsx.discord.listeners;

import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.events.MuteStatusChangeEvent;
import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.api.v2.discord.events.DiscordMessageEvent;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDiscordMessage(DiscordMessageEvent event) {
        jda.sendMessage(event.getType(), event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMute(MuteStatusChangeEvent event) {
        if (event.getValue()) {
            sendDiscordMessage("mute", event.getAffected().getBase().getName() + " has been muted.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        sendDiscordMessage("chat", event.getPlayer().getDisplayName() + ": " + event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        sendDiscordMessage("join", event.getPlayer().getName() + " has joined the server.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        sendDiscordMessage("quit", event.getPlayer().getName() + " has left the server.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        sendDiscordMessage("death", event.getEntity().getName() + " has died!");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        sendDiscordMessage("kick", event.getPlayer().getName() + " has been kicked.");
    }

    private void sendDiscordMessage(String type, String message) {
        final DiscordMessageEvent discordMessageEvent = new DiscordMessageEvent(type, FormatUtil.stripFormat(message));
        if (Bukkit.getServer().isPrimaryThread()) {
            Bukkit.getPluginManager().callEvent(discordMessageEvent);
        } else {
            Bukkit.getScheduler().runTask(jda.getPlugin(), () -> Bukkit.getPluginManager().callEvent(discordMessageEvent));
        }
    }
}
