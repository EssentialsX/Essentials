package com.earth2me.essentials.discord;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IEssentials;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.event.Listener;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EssentialsDiscordListener extends ListenerAdapter implements Listener, IConf {

    private static final Logger logger = Logger.getLogger("EssentialsDiscord");
    private JDA jda = null;
    private final File dataFolder;
    private final EssentialsConf config;
    private final transient IEssentials ess;

    public EssentialsDiscordListener(File dataFolder, IEssentials ess) {
        this.ess = ess;
        this.dataFolder = dataFolder;
        this.config = new EssentialsConf(new File(dataFolder, "config.yml"));
        config.setTemplateName("/config.yml", EssentialsDiscord.class);
        reloadConfig();
    }

    public void startup() throws LoginException, InterruptedException {
        if (jda != null) {
            jda.removeEventListener(jda.getRegisteredListeners());
        }

        logger.log(Level.INFO, "Attempting to login to discord...");
        this.jda = JDABuilder.createDefault(config.getString("token"))
                .addEventListeners(this)
                .setAutoReconnect(true)
                .setContextEnabled(false)
                .build()
                //We don't want to be async here since players could *possibly* chat before jda starts
                .awaitReady();
        logger.log(Level.INFO, "Successfully logged in as " + jda.getSelfUser().getAsTag());
    }

    @Override
    public void reloadConfig() {
        config.load();
    }
}
