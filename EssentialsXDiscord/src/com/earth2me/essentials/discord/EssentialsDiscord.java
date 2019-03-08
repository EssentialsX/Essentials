package com.earth2me.essentials.discord;

import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.metrics.Metrics;
import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsDiscord extends JavaPlugin implements IEssentialsModule, IEssentialsDiscord {

    private transient Metrics metrics = null;
    private DiscordApi api;

    private DiscordSettings settings = null;
    private DiscordFormatter formatter = null;

    @Override
    public void onEnable() {
        final PluginManager pm = getServer().getPluginManager();
        final IEssentials ess = (IEssentials) pm.getPlugin("Essentials");
        if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, tl("versionMismatchAll"));
        }

        if (!ess.isEnabled()) {
            this.setEnabled(false);
            return;
        }

        settings = new DiscordSettings(ess, this);
        ess.addReloadListener(settings);

        formatter = new DiscordFormatter(ess);

        getLogger().log(Level.INFO, "Attempting to login with the token provided...");
        try {
            new DiscordApiBuilder()
                .setToken(settings.getBotToken())
                .login()
                .thenAccept(api -> {
                    this.api = api;
                    getLogger().log(Level.INFO,"Successfully logged in as " + api.getYourself().getDiscriminatedName());
                });
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Failed to log in!", ex);
            this.setEnabled(false);
            return;
        }

        // Register Bukkit Event Listeners
        final BukkitListener bukkitListener = new BukkitListener(this, api, settings);
        pm.registerEvents(bukkitListener, this);

        // Add the listener for the Discord Server to MC Chat
        api.addMessageCreateListener(new DiscordListener(api));

        // Check if Metrics are null, and if not, enable them.
        if (metrics == null) {
            metrics = new Metrics(this);
        }
    }

    public CompletableFuture<Void> sendChatMessage(MessageType type, Player player, String message) {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("player", player);
        tokenMap.put("message", message);
        tokenMap.put("msg", message);

        return sendFormattedMessage(type, tokenMap);
    }

    public CompletableFuture<Void> sendPlayerMessage(MessageType type, Player player) {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("player", player);

        return sendFormattedMessage(type, tokenMap);
    }

    public CompletableFuture<Void> sendStatusMessage(MessageType type, Player controller, Player affected, String message) {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("player", affected);
        tokenMap.put("target", affected);
        tokenMap.put("affected", affected);
        tokenMap.put("by", controller);
        tokenMap.put("controller", controller);

        return sendFormattedMessage(type, tokenMap);
    }

    public CompletableFuture<Void> sendFormattedMessage(MessageType type, Map<String, Object> tokenMap) {
        List<CompletableFuture> futures = new ArrayList<>();

        for (DiscordSettings.ChannelDefinition channelDef : settings.getChannelDefinitions(type.getConfigName())) {
            Optional<TextChannel> channel = api.getTextChannelById(channelDef.getChannelId());
            if (!channel.isPresent()) continue;

            String formatted = formatter.format(channelDef, tokenMap);
            futures.add(channel.get().sendMessage(formatted));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public CompletableFuture<Void> sendRawMessage(MessageType type, String message) {
        return sendRawMessage(type.getConfigName(), message);
    }

    public CompletableFuture<Void> sendRawMessage(String type, String message) {
        CompletableFuture[] futures = settings.getChannelDefinitions(type).parallelStream()
            .map(def -> api.getTextChannelById(def.getChannelId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(channel -> channel.sendMessage(message))
            .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures);
    }
}
