package com.earth2me.essentials.discord;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

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
        return config.getLong("channels.primary", 0);
    }

    private List<ChannelDefinition> channelDefinitions;

    public List<ChannelDefinition> getChannelDefinitions() {
        return channelDefinitions;
    }

    public List<ChannelDefinition> getChannelDefinitions(final String type) {
        List<ChannelDefinition> results = new ArrayList<>();
        for (ChannelDefinition channelDefinition : channelDefinitions) {
            if (channelDefinition.getType().equalsIgnoreCase(type)) {
                results.add(channelDefinition);
            }
        }
        return results;
    }

    private List<ChannelDefinition> _getChannelDefinitions() {
        List<ConfigurationSection> sections = (List<ConfigurationSection>) config.getList("channels");
        List<ChannelDefinition> definitions = new ArrayList<>();

        if (sections != null) {
            for (ConfigurationSection section : sections) {
                String type = section.getString("type", null);
                String channelId = section.getString("channel-id", null);
                boolean isWebhook = section.getBoolean("use-webhook", false);
                String format = section.getString("format", null);

                if (type == null) {
                    plugin.getLogger().severe(tl("discordDefTypeMissing", section.toString(), section.getCurrentPath()));
                    continue;
                } else if (channelId == null) {
                    plugin.getLogger().severe(tl("discordDefIdMissing", section.toString(), section.getCurrentPath()));
                    continue;
                } else if (format == null) {
                    plugin.getLogger().severe(tl("discordDefFormatMissing", section.toString(), section.getCurrentPath()));
                    continue;
                }

                definitions.add(new ChannelDefinition(type, channelId, isWebhook, format));
            }
        }
        return definitions;
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
        channelDefinitions = _getChannelDefinitions();
    }

    public static class ChannelDefinition {
        private final String type;
        private final String channelId;
        private final boolean webhook;
        private final String format;

        public ChannelDefinition(String type, String channelId, boolean webhook, String format) {
            this.type = type;
            this.channelId = channelId;
            this.webhook = webhook;
            this.format = format;
        }

        public String getType() {
            return type;
        }

        public String getChannelId() {
            return channelId;
        }

        public boolean isWebhook() {
            return webhook;
        }

        public String getFormat() {
            return format;
        }
    }
}
