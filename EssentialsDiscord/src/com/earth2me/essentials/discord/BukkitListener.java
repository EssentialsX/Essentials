package com.earth2me.essentials.discord;

import net.ess3.api.events.DiscordMessageEvent;
import net.ess3.api.events.MuteStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public class BukkitListener implements Listener {
    private static final Logger logger = Logger.getLogger("EssentialsDiscord");

    private final EssentialsDiscord plugin;
    private final EssentialsJDA jda;

    public BukkitListener(EssentialsDiscord plugin, EssentialsJDA jda) {
        this.plugin = plugin;
        this.jda = jda;
    }

    //Essentials Events

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDiscordMessage(DiscordMessageEvent event) {
        jda.sendMessage(plugin.getSettings().getMessageChannel(event.getType()).getId(), event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMute(MuteStatusChangeEvent event) {
        if (event.getValue()) {
            sendDiscordMessage("mute", event.getAffected().getBase().getName() + " has been muted.");
        }
    }

    //Bukkit Events

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        for (DiscordSettings.ChannelDefinition channelDefinition : plugin.getSettings().getChannelDefinitions()) {
            if (event.getPlayer().hasPermission("essentials.discord.channel." + channelDefinition.getName() + ".send")) {
                sendDiscordMessage("chat", event.getPlayer().getDisplayName() + ": " + event.getMessage());
            }
        }
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
        final DiscordMessageEvent discordMessageEvent = new DiscordMessageEvent(type, message);
        if (Bukkit.getServer().isPrimaryThread()) {
            Bukkit.getPluginManager().callEvent(discordMessageEvent);
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Bukkit.getPluginManager().callEvent(discordMessageEvent));
        }
    }
}
