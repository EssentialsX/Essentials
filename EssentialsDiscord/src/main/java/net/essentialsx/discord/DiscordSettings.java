package net.essentialsx.discord;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import com.earth2me.essentials.utils.FormatUtil;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordSettings implements IConf {
    private final EssentialsConf config;
    private final EssentialsDiscord plugin;

    private final Map<String, Long> nameToChannelIdMap = new HashMap<>();
    private final Map<Long, List<String>> channelIdToNamesMap = new HashMap<>();

    private OnlineStatus status;
    private Activity statusActivity;

    private MessageFormat discordToMcFormat;
    private MessageFormat mcToDiscordFormat;
    private MessageFormat tempMuteFormat;
    private MessageFormat tempMuteReasonFormat;
    private MessageFormat permMuteFormat;
    private MessageFormat permMuteReasonFormat;
    private MessageFormat unmuteFormat;

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

    public boolean isShowDiscordAttachments() {
        return config.getBoolean("show-discord-attachments", true);
    }

    public List<String> getPermittedFormattingRoles() {
        return config.getStringList("permit-formatting-roles");
    }

    public OnlineStatus getStatus() {
        return status;
    }

    public Activity getStatusActivity() {
        return statusActivity;
    }

    public MessageFormat getDiscordToMcFormat() {
        return discordToMcFormat;
    }

    public MessageFormat getMcToDiscordFormat() {
        return mcToDiscordFormat;
    }

    public MessageFormat getTempMuteFormat() {
        return tempMuteFormat;
    }

    public MessageFormat getTempMuteReasonFormat() {
        return tempMuteReasonFormat;
    }

    public MessageFormat getPermMuteFormat() {
        return permMuteFormat;
    }

    public MessageFormat getPermMuteReasonFormat() {
        return permMuteReasonFormat;
    }

    public MessageFormat getUnmuteFormat() {
        return unmuteFormat;
    }

    private MessageFormat generateMessageFormat(String node, String defaultStr, boolean format, String... arguments) {
        String pattern = config.getString("messages." + node);
        pattern = pattern == null ? defaultStr : pattern;
        pattern = format ? FormatUtil.replaceFormat(pattern) : FormatUtil.stripFormat(pattern);
        for (int i = 0; i < arguments.length; i++) {
            pattern = pattern.replace("{" + arguments[i] + "}", "{" + i + "}");
        }
        return new MessageFormat(pattern);
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

        //noinspection ConstantConditions
        final String activity = config.getString("presence.activity", "default").trim().toUpperCase().replace("CUSTOM", "CUSTOM_STATUS");
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
            //noinspection ConstantConditions
            statusActivity = Activity.of(activityType, config.getString("presence.message", "Minecraft"));
        }

        discordToMcFormat = generateMessageFormat("discord-to-mc", "&6[#{channel}] &3{fullname}&7: &f{message}", true,
                "channel", "username", "tag", "fullname", "nickname", "color", "message");
        mcToDiscordFormat = generateMessageFormat("mc-to-discord", "[{world}] {displayname} > {message}", false,
                "username", "displayname", "message", "world", "prefix", "suffix");
        unmuteFormat = generateMessageFormat("unmute", "{displayname} has been unmuted.", false, "username", "displayname");
        tempMuteFormat = generateMessageFormat("temporary-mute", "{controllerdisplayname} muted {displayname} for {time}", false,
                "username", "displayname", "controllername", "controllerdisplayname", "time");
        permMuteFormat = generateMessageFormat("permanent-mute", "{controllerdisplayname} permanently muted {displayname}", false,
                "username", "displayname", "controllername", "controllerdisplayname");
        tempMuteReasonFormat = generateMessageFormat("temporary-mute-reason", "{controllerdisplayname} muted {displayname} for {time} with reason: {reason}", false,
                "username", "displayname", "controllername", "controllerdisplayname", "time", "reason");
        permMuteReasonFormat = generateMessageFormat("permanent-mute-reason", "{controllerdisplayname} permanently muted {displayname} with reason: {reason}", false,
                "username", "displayname", "controllername", "controllerdisplayname", "reason");

        plugin.onReload();
    }
}
