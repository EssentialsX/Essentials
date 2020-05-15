package com.earth2me.essentials.discord.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class DiscordUtils {
    private static final Logger logger = Logger.getLogger("EssentialsDiscord");
    private static final IEssentials ess = Objects.requireNonNull((IEssentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials"));

    public static void sendMessage(JDA jda, String textChannelId, String message) {
        TextChannel channel = jda.getTextChannelById(textChannelId);
        if (channel == null) {
            logger.log(Level.WARNING, tl("discordInvalidChannelId"));
            return;
        }
        channel.sendMessage(message).queue(success -> {}, fail -> {
            if (ess.getSettings().isDebug()) {
                fail.printStackTrace();
            }
            logger.log(Level.SEVERE, tl("discordMessageError", fail.getMessage()));
        });
    }
}
