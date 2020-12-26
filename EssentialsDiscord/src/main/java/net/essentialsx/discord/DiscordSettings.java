package net.essentialsx.discord;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordSettings implements IConf {
    private final EssentialsConf config;
    private final EssentialsDiscord plugin;
    private final Map<String, Long> nameToChannelIdMap = new HashMap<>();
    private final Map<Long, List<String>> channelIdToNamesMap = new HashMap<>();

    public DiscordSettings(EssentialsDiscord plugin) {
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

    public long getChannelId(String key) {
        try {
            return Long.parseLong(key);
        } catch (NumberFormatException ignored) {
            return nameToChannelIdMap.getOrDefault(key, 0L);
        }
    }

    public List<String> getKeysFromChannelId(long channelId) {
        return channelIdToNamesMap.get(channelId);
    }

    public String getMessageChannel(String key) {
        return config.getString("message-types." + key, "none");
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

        // Build channel maps
        nameToChannelIdMap.clear();
        channelIdToNamesMap.clear();
        final ConfigurationSection section = config.getConfigurationSection("channels");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (section.isLong(key)) {
                    final long value = section.getLong(key);
                    nameToChannelIdMap.put(key, value);
                    channelIdToNamesMap.computeIfAbsent(value, o -> new ArrayList<>()).add(key);
                }
            }
        }

        plugin.onReload();
    }
}
