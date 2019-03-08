package com.earth2me.essentials.discord;

import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.metrics.Metrics;
import net.ess3.api.IEssentials;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsDiscord extends JavaPlugin implements IEssentialsModule {

    private transient Metrics metrics = null;
    private DiscordSettings settings = null;
    private DiscordApi api;

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
        final BukkitListener bukkitListener = new BukkitListener(ess, api, settings);
        pm.registerEvents(bukkitListener, this);

        // Add the listener for the Discord Server to MC Chat
        api.addMessageCreateListener(new DiscordListener(api));

        // Check if Metrics are null, and if not, enable them.
        if (metrics == null) {
            metrics = new Metrics(this);
        }
    }
}
