package com.earth2me.essentials.discord;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DiscordSettings implements IConf {
    private final EssentialsConf config;
    private final EssentialsDiscord plugin;

    public DiscordSettings(final EssentialsDiscord plugin) {
        this.plugin = plugin;
        this.config = new EssentialsConf(new File(plugin.getDataFolder(), "config.yml"));
        config.setTemplateName("/config.yml", EssentialsDiscord.class);
        reloadConfig();
    }

    public String getBotToken() {
        return config.getString("token", "");
    }

    public long getGuildId() {
        return config.getLong("guild", 0);
    }

    public long getPrimaryChannelId() {
        return config.getLong("channels.primary.id", 0);
    }

    public long getChannelId(String name) {
        return config.getLong("channel." + name + ".id", 0);
    }

    private List<ChannelDefinition> channelDefinitions;

    public List<ChannelDefinition> getChannelDefinitions() {
        return channelDefinitions;
    }

    public ChannelDefinition getChannelDefinition(String name) {
        for (ChannelDefinition channelDefinition : channelDefinitions) {
            if (channelDefinition.getName().equals(name)) {
                return channelDefinition;
            }
        }
        return null;
    }

    public ChannelDefinition getChannelDefinition(long id) {
        for (ChannelDefinition channelDefinition : channelDefinitions) {
            if (channelDefinition.getId() == id) {
                return channelDefinition;
            }
        }
        return null;
    }

    public ChannelDefinition getMessageChannel(String type) {
        return getChannelDefinition(config.getString("messages." + type, "primary"));
    }

    private void _loadChannelDefinitions() {
        channelDefinitions = new ArrayList<>();
        ConfigurationSection channelsSection = config.getConfigurationSection("channels");
        if (channelsSection == null) {
            return;
        }
        for (String channel : channelsSection.getKeys(false)) {
            long id = config.getLong("channels." + channel + ".id", 0);
            if (id > 0) {
                channelDefinitions.add(new ChannelDefinition(channel, id));
            }
        }
    }

    public String getStatusActivity() {
        return config.getString("status.activity", "default");
    }

    public String getStatusMessage() {
        return config.getString("status.message", "Minecraft");
    }

    @Override
    public void reloadConfig() {
        config.load();
        _loadChannelDefinitions();
    }

    public static class ChannelDefinition {
        private final String name;
        private final long id;

        public ChannelDefinition(String name, long id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public long getId() {
            return id;
        }
    }
}
