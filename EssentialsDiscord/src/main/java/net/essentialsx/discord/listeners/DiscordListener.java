package net.essentialsx.discord.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.essentialsx.discord.EssentialsJDA;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DiscordListener extends ListenerAdapter {
    private final EssentialsJDA plugin;

    public DiscordListener(EssentialsJDA plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) {
            return;
        }

        final List<String> keys = plugin.getPlugin().getSettings().getKeysFromChannelId(event.getChannel().getIdLong());
        if (keys == null || keys.size() == 0) {
            return;
        }

        for (String group : keys) {
            Bukkit.broadcast("[Discord] <" + event.getAuthor().getAsTag() + ">: " + event.getMessage().getContentDisplay(), "essentials.discord.receive." + group);
        }
    }
}
