package com.earth2me.essentials.discord;

import com.earth2me.essentials.discord.utils.MessageType;
import net.ess3.api.events.MuteStatusChangeEvent;
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

    //Bukkit Events
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        jda.sendMessage(event.getPlayer().getName() + " has joined the server.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        jda.sendMessage(event.getPlayer().getName() + " has left the server.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        jda.sendMessage(event.getEntity().getName() + " has died!");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        jda.sendMessage(event.getPlayer().getDisplayName() + ": " + event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        jda.sendMessage(event.getPlayer().getName() + " has been kicked.");
    }

    //Essentials Events
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMute(MuteStatusChangeEvent event) {
        if (event.getValue()) {
            jda.sendMessage(event.getAffected().getBase().getName() + " has been muted.");
        }
    }
}
