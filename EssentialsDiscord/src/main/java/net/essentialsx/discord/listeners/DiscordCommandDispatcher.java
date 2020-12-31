package net.essentialsx.discord.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.util.DiscordCommandSender;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class DiscordCommandDispatcher extends ListenerAdapter {
    private final EssentialsJDA jda;

    public DiscordCommandDispatcher(EssentialsJDA jda) {
        this.jda = jda;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (jda.getConsoleWebhook() != null && jda.getSettings().getConsoleChannelDef().equals(event.getChannel().getId())
                && !event.isWebhookMessage() && !event.getAuthor().isBot()) {
            Bukkit.getScheduler().runTask(jda.getPlugin(), () ->
                    Bukkit.dispatchCommand(new DiscordCommandSender(jda, event.getMessage(), Bukkit.getConsoleSender()), event.getMessage().getContentRaw()));
        }
    }
}
