package com.earth2me.essentials.discord.utils;

import com.earth2me.essentials.discord.DiscordSettings;
import com.earth2me.essentials.discord.EssentialsDiscord;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class DiscordUtils {
    private static final Logger logger = Logger.getLogger("EssentialsDiscord");
    private static final EssentialsDiscord plugin = Objects.requireNonNull((EssentialsDiscord) Bukkit.getServer().getPluginManager().getPlugin("EssentialsDiscord"));

    public static void sendMessage(JDA jda, MessageType type, String message) {
        for (DiscordSettings.ChannelDefinition channel : plugin.getSettings().getChannelDefinitions(type.getConfigName())) {
            sendMessage(jda, channel.getChannelId(), message);
        }
    }

    public static void sendMessage(JDA jda, String textChannelId, String message) {
        TextChannel channel = jda.getTextChannelById(textChannelId);
        if (channel == null) {
            logger.log(Level.WARNING, tl("discordInvalidChannelId"));
            return;
        }
        channel.sendMessage(message).queue(success -> {}, fail -> {
            if (plugin.getParent().getSettings().isDebug()) {
                fail.printStackTrace();
            }
            logger.log(Level.SEVERE, tl("discordMessageError", fail.getMessage()));
        });
    }
}
