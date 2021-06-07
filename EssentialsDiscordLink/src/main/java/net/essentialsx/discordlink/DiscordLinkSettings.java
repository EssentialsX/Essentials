package net.essentialsx.discordlink;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.config.EssentialsConfiguration;

import java.io.File;

public class DiscordLinkSettings implements IConf {
    private final EssentialsDiscordLink plugin;
    private final EssentialsConfiguration config;

    private LinkPolicy linkPolicy;

    public DiscordLinkSettings(EssentialsDiscordLink plugin) {
        this.plugin = plugin;
        this.config = new EssentialsConfiguration(new File(plugin.getDataFolder(), "config.yml"), "/config.yml", EssentialsDiscordLink.class);
        reloadConfig();
    }

    public LinkPolicy getLinkPolicy() {
        return linkPolicy;
    }

    public String getInviteUrl() {
        return config.getString("discord-url", "https://discord.gg/invite-code");
    }

    public boolean isBlockUnlinkedChat() {
        return config.getBoolean("block-unlinked-chat", false);
    }

    public enum LinkPolicy {
        KICK,
        FREEZE,
        NONE;

        static LinkPolicy fromName(final String name) {
            for (LinkPolicy policy : values()) {
                if (policy.name().equalsIgnoreCase(name)) {
                    return policy;
                }
            }
            return LinkPolicy.NONE;
        }
    }

    @Override
    public void reloadConfig() {
        config.load();

        linkPolicy = LinkPolicy.fromName(config.getString("link-policy", null));
    }
}
