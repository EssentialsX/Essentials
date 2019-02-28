package com.earth2me.essentials.discord;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import net.ess3.api.IEssentials;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;

public class DiscordSettings implements IConf {
    private final EssentialsConf config;
    private EssentialsDiscord plugin;

    public DiscordSettings(final IEssentials ess, final EssentialsDiscord plugin) {
        this.plugin = plugin;
        this.config = new EssentialsConf(new File(plugin.getDataFolder(), "config.yml"));
        config.setTemplateName("/config.yml", EssentialsDiscord.class);

        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        botToken = config.getString("token");
        serverId = config.getInt("server-id");
        channelDefinitions = loadChannelDefinitions();
    }

    private String botToken;

    public String getBotToken() {
        return botToken;
    }

    private int serverId;

    public int getServerId() {
        return serverId;
    }

    private List<ChannelDefinition> channelDefinitions;

    public List<ChannelDefinition> getChannelDefinitions() {
        return channelDefinitions;
    }

    public List<ChannelDefinition> getChannelDefinitions(final String type) {
        return channelDefinitions.stream()
            .filter(def -> def.type.equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }

    private List<ChannelDefinition> loadChannelDefinitions() {
        List<ConfigurationSection> sections = (List<ConfigurationSection>) config.getList("channels");
        List<ChannelDefinition> definitions = new ArrayList<>();

        for (ConfigurationSection section : sections) {
            String type = section.getString("type", null);
            int channelId = section.getInt("channel-id", -1);
            String format = section.getString("format", null);
            boolean useWebhook = section.getBoolean("use-webhook", false);

            if (type == null) {
                plugin.getLogger().severe(tl("discordDefTypeMissing", section.toString(), section.getCurrentPath()));
                continue;
            } else if (channelId < 0) {
                plugin.getLogger().severe(tl("discordDefIdMissing", section.toString(), section.getCurrentPath()));
                continue;
            } else if (format == null) {
                plugin.getLogger().severe(tl("discordDefFormatMissing", section.toString(), section.getCurrentPath()));
                continue;
            }


            definitions.add(new ChannelDefinition(type, channelId, useWebhook, format));
        }

        return definitions;
    }

    public static class ChannelDefinition {
        private final String type;
        private final int channelId;
        private final boolean useWebhook;
        private final String format;

        public ChannelDefinition(String type, int channelId, boolean useWebhook, String format) {
            this.type = type;
            this.channelId = channelId;
            this.useWebhook = useWebhook;
            this.format = format;
        }

        public String getType() {
            return type;
        }

        public int getChannelId() {
            return channelId;
        }

        public boolean isUseWebhook() {
            return useWebhook;
        }

        public String getFormat() {
            return format;
        }
    }
}
