package net.essentialsx.discord;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DiscordSettings implements IConf {
    private final EssentialsConf config;
    private final EssentialsDiscord plugin;

    private final Map<String, Long> nameToChannelIdMap = new HashMap<>();
    private final Map<Long, List<String>> channelIdToNamesMap = new HashMap<>();

    private OnlineStatus status;
    private Activity statusActivity;

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

    public OnlineStatus getStatus() {
        return status;
    }

    public Activity getStatusActivity() {
        return statusActivity;
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

        // Presence stuff
        status = OnlineStatus.fromKey(config.getString("presence.status", "online"));
        if (status == OnlineStatus.UNKNOWN) {
            // Default invalid status to online
            status = OnlineStatus.ONLINE;
        }

        final String activity = Objects.requireNonNull(config.getString("presence.activity", "default")).trim().toUpperCase().replace("CUSTOM", "CUSTOM_STATUS");
        statusActivity = null;
        Activity.ActivityType activityType = null;
        try {
            if (!activity.equals("NONE")) {
                activityType = Activity.ActivityType.valueOf(activity);
            }
        } catch (IllegalArgumentException e) {
            activityType = Activity.ActivityType.DEFAULT;
        }
        if (activityType != null) {
            statusActivity = Activity.of(activityType, Objects.requireNonNull(config.getString("presence.message", "Minecraft")));
        }

        plugin.onReload();
    }
}
