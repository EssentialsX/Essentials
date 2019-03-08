package com.earth2me.essentials.discord;

import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.MuteStatusChangeEvent;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.javacord.api.DiscordApi;

import java.util.logging.Logger;

/**
 * Created by GlareMasters
 * Date: 2/27/2019
 * Time: 8:13 PM
 */
public class BukkitListener implements Listener {
    private static final Logger logger = Logger.getLogger("EssentialsDiscord");

    private final IEssentialsDiscord plugin;
    private final DiscordApi api;
    private final DiscordSettings settings;

    public BukkitListener(EssentialsDiscord plugin, DiscordApi api, DiscordSettings settings) {
        this.plugin = plugin;
        this.api = api;
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        plugin.sendChatMessage(MessageType.GLOBAL_CHAT, player, message);
        // TODO: local chat
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        plugin.sendPlayerMessage(MessageType.PLAYER_JOIN, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        plugin.sendPlayerMessage(MessageType.PLAYER_QUIT, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        plugin.sendChatMessage(MessageType.PLAYER_DEATH, event.getEntity(), event.getDeathMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        plugin.sendRawMessage(MessageType.PLAYER_ADVANCEMENT,
            player.getName() + " has just completed " + advancement.getKey().getKey());
        // TODO: format advancement messages
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMute(MuteStatusChangeEvent event) {
        Player player = event.getAffected().getBase();
        if (event.getValue()) {
            plugin.sendRawMessage(MessageType.PLAYER_MUTE, player.getName() + " has been muted on the server.");
        }
        // TODO: format controller/affected messages
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        plugin.sendRawMessage(MessageType.PLAYER_KICK, player.getName() + " has been kicked from the server.");
        // TODO: ban message
        // TODO: format controller/affected messages
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfkChange(AfkStatusChangeEvent event) {
        plugin.sendChatMessage(MessageType.PLAYER_AWAY, event.getAffected().getBase(), event.);
    }
}
