package net.essentialsx.discord.listeners;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.essentialsx.discord.EssentialsDiscord;

public class DiscordListener extends ListenerAdapter {
    private final EssentialsDiscord plugin;

    public DiscordListener(EssentialsDiscord plugin) {
        this.plugin = plugin;
    }
}
