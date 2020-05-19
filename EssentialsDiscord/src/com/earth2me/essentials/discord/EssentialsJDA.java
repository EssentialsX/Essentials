package com.earth2me.essentials.discord;

import com.earth2me.essentials.discord.utils.MessageType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsJDA {

    private static final Logger logger = Logger.getLogger("EssentialsDiscord");

    private JDA jda = null;
    private final EssentialsDiscord plugin;

    public EssentialsJDA(EssentialsDiscord plugin) {
        this.plugin = plugin;
    }

    public void startup() throws LoginException, InterruptedException {
        if (jda != null) {
            jda.removeEventListener(jda.getRegisteredListeners());
            HandlerList.unregisterAll(plugin);
        }

        logger.log(Level.INFO, "Attempting to login to discord...");
        this.jda = JDABuilder.createDefault(plugin.getSettings().getBotToken())
                .addEventListeners(new DiscordListener(plugin))
                .setAutoReconnect(true)
                .setContextEnabled(false)
                .build()
                //We don't want to be async here since players could *possibly* chat before jda starts
                .awaitReady();
        this.setActivity(plugin.getSettings().getStatusActivity(), plugin.getSettings().getStatusMessage());
        logger.log(Level.INFO, "Successfully logged in as " + jda.getSelfUser().getAsTag());
        Bukkit.getServer().getPluginManager().registerEvents(new BukkitListener(plugin, this), plugin);
    }

    public void sendMessage(String message) {
        TextChannel channel = this.jda.getTextChannelById(plugin.getSettings().getPrimaryChannelId());
        if (channel == null) {
            logger.log(Level.WARNING, tl("discordInvalidChannelId"));
            return;
        }
        String messageStripped = ChatColor.stripColor(message);
        channel.sendMessage(messageStripped).queue(success -> {
        }, fail -> {
            if (plugin.getParent().getSettings().isDebug()) {
                fail.printStackTrace();
            }
            logger.log(Level.SEVERE, tl("discordMessageError", fail.getMessage()));
        });
    }

    public void sendMessage(MessageType type, String message) {
        for (DiscordSettings.ChannelDefinition channel : plugin.getSettings().getChannelDefinitions(type.getConfigName())) {
            sendMessage(channel.getChannelId(), message);
        }
    }

    public void sendMessage(String textChannelId, String message) {
        TextChannel channel = this.jda.getTextChannelById(textChannelId);
        if (channel == null) {
            logger.log(Level.WARNING, tl("discordInvalidChannelId"));
            return;
        }
        String messageStripped = ChatColor.stripColor(message);
        channel.sendMessage(messageStripped).queue(success -> {
        }, fail -> {
            if (plugin.getParent().getSettings().isDebug()) {
                fail.printStackTrace();
            }
            logger.log(Level.SEVERE, tl("discordMessageError", fail.getMessage()));
        });
    }

    public void setActivity(String activity, String message) {
        Activity.ActivityType type;
        try {
            type = Activity.ActivityType.valueOf(activity.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            type = Activity.ActivityType.DEFAULT;
        }
        String name = message == null || message.isEmpty() ? "Minecraft" : message;
        this.jda.getPresence().setActivity(Activity.of(type, name));
    }
}
