package net.essentialsx.discord.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.essentialsx.discord.EssentialsDiscord;
import org.jetbrains.annotations.NotNull;

public class DiscordListener extends ListenerAdapter {
    private final EssentialsDiscord plugin;

    public DiscordListener(EssentialsDiscord plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) {
            return;
        }

        //TODO FIGURE OUT WHICH CHANNELS WE READ FROM AND HOW THE FUCK WE'RE DOING IT
    }
}
