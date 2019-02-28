package com.earth2me.essentials.discord;

import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by GlareMasters
 * Date: 2/27/2019
 * Time: 8:13 PM
 */
public class BukkitListener implements Listener {
    private static final Logger logger = Logger.getLogger("EssentialsDiscord");

    private final transient IEssentials ess;
    private final DiscordApi api;
    private final DiscordSettings settings;

    public BukkitListener(IEssentials ess, DiscordApi api, DiscordSettings settings) {
        this.ess = ess;
        this.api = api;
        this.settings = settings;
    }

    private void sendMessage(MessageType type, String message) {
        sendMessage(type.getConfigName(), message);
    }

    private void sendMessage(String type, String message) {
        settings.getChannelDefinitions(type).parallelStream()
            .map(def -> api.getTextChannelById(def.getChannelId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(channel -> channel.sendMessage(message));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        sendMessage("chat", player.getDisplayName() + ": " + message);
        // TODO: actually use formatting from channel definition
    }
}
