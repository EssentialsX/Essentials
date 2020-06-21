package com.earth2me.essentials.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;

import javax.security.auth.login.LoginException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsJDA {

    private static final Logger logger = Logger.getLogger("EssentialsDiscord");

    private JDA jda = null;
    private Guild guild;
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
        try {
            this.guild = Objects.requireNonNull(this.jda.getGuildById(plugin.getSettings().getGuildId()));
        } catch (NullPointerException e) {
            logger.severe("No guild configured!");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
        Bukkit.getServer().getPluginManager().registerEvents(new BukkitListener(plugin, this), plugin);
    }

    public void sendMessage(String message) {
        sendMessage(plugin.getSettings().getPrimaryChannelId(), message);
    }

    public void sendMessage(long channelId, String message) {
        TextChannel channel = this.jda.getTextChannelById(channelId);
        if (channel == null) {
            logger.log(Level.WARNING, tl("discordInvalidChannelId"));
            return;
        }
        String msg = ChatColor.stripColor(message)
                .replaceAll("@here", "@\u200bhere")
                .replaceAll("@everyone", "@\u200beveryone");
        if (msg.contains("#")) {
            for (TextChannel textChannel : guild.getTextChannels()) {
                msg = msg.replaceAll("#" + textChannel.getName(), textChannel.getAsMention());
            }
        }
        if (Pattern.compile(":.+:").matcher(msg).find()) {
            for (Emote emote : guild.getEmotes()) {
                msg = msg.replaceAll(":" + emote.getName() + ":", emote.getAsMention());
            }
        }
        if (msg.contains("@")) {
            for (Role role : guild.getRoles()) {
                msg = msg.replaceAll("@" + role.getName(), role.getAsMention());
            }
            for (Member member : guild.getMembers()) {
                msg = msg.replaceAll("@" + member.getEffectiveName(), member.getAsMention());
            }
        }
        channel.sendMessage(msg).queue(success -> {
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
