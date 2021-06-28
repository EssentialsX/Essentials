package net.essentialsx.discord.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.essentialsx.discord.JDADiscordService;
import net.essentialsx.discord.util.DiscordCommandSender;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class DiscordCommandDispatcher extends ListenerAdapter {
    private final JDADiscordService jda;
    private String channelId = null;

    public DiscordCommandDispatcher(JDADiscordService jda) {
        this.jda = jda;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (jda.getConsoleWebhook() != null && event.getChannel().getId().equals(channelId)
                && !event.isWebhookMessage() && !event.getAuthor().isBot()) {
            Bukkit.getScheduler().runTask(jda.getPlugin(), () ->
                    Bukkit.dispatchCommand(new DiscordCommandSender(jda, Bukkit.getConsoleSender(), message ->
                            event.getMessage().reply(message).queue()).getSender(), event.getMessage().getContentRaw()));
        }
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
