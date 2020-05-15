package com.earth2me.essentials.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EssentialsDiscordListener extends ListenerAdapter {
    private static final Logger logger = Logger.getLogger("EssentialsDiscord");

    private JDA jda = null;
    private final EssentialsDiscord plugin;

    public EssentialsDiscordListener(EssentialsDiscord plugin) {
        this.plugin = plugin;
    }

    public void startup() throws LoginException, InterruptedException {
        if (jda != null) {
            jda.removeEventListener(jda.getRegisteredListeners());
        }

        logger.log(Level.INFO, "Attempting to login to discord...");
        this.jda = JDABuilder.createDefault(plugin.getSettings().getBotToken())
                .addEventListeners(this)
                .setAutoReconnect(true)
                .setContextEnabled(false)
                .build()
                //We don't want to be async here since players could *possibly* chat before jda starts
                .awaitReady();
        logger.log(Level.INFO, "Successfully logged in as " + jda.getSelfUser().getAsTag());
        Bukkit.getServer().getPluginManager().registerEvents(new BukkitListener(plugin, jda), plugin);
    }
}
