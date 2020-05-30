package com.earth2me.essentials.discord;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class DiscordListener extends ListenerAdapter {

    private final EssentialsDiscord plugin;

    public DiscordListener(EssentialsDiscord plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) {
            return;
        }
        DiscordSettings.ChannelDefinition channelDefinition = plugin.getSettings().getChannelDefinition(event.getChannel().getIdLong());
        if (channelDefinition == null) {
            return;
        }
        String message = event.getAuthor().getAsTag() + ": " + event.getMessage().getContentStripped();
        String permission = "essentials.discord.channel." + channelDefinition.getName() + ".read";
        Bukkit.broadcast(message, permission);
    }
}
