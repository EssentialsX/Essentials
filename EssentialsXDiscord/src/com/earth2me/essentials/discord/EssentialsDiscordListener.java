package com.earth2me.essentials.discord;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by GlareMasters
 * Date: 2/27/2019
 * Time: 8:13 PM
 */
public class EssentialsDiscordListener implements Listener, IConf {
    private static final Logger logger = Logger.getLogger("EssentialsDiscord");
    private DiscordApi api;
    private File dataFolder;
    private final EssentialsConf config;
    private final transient IEssentials ess;

    private String logChannelID;
    private TextChannel logChannel;

    public EssentialsDiscordListener(File dataFolder, IEssentials ess) {
        this.ess = ess;
        this.dataFolder = dataFolder;
        this.config = new EssentialsConf(new File(dataFolder, "config.yml"));
        config.setTemplateName("/config.yml", EssentialsDiscord.class);
        reloadConfig();
    }

    @EventHandler
    public void onLoad(PluginEnableEvent event) {
        logger.log(Level.INFO, "Attempting to login with the token provided...");
        try {
            api = new DiscordApiBuilder().setToken(config.getString("token")).login().join();
            logger.log(Level.INFO, "Successfully logged in as " + api.getYourself().getDiscriminatedName());
            logChannelID = config.getString("channel-id");
            if (api.getTextChannelById(logChannelID).isPresent()) {
                logChannel = api.getTextChannelById(logChannelID).get();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        logChannel.sendMessage(player.getName() + " - " + message);
    }

    @Override
    public void reloadConfig() {
        config.load();
    }
}
