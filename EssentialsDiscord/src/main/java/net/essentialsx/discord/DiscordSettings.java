package net.essentialsx.discord;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.config.ConfigurateUtil;
import com.earth2me.essentials.config.EssentialsConfiguration;
import com.earth2me.essentials.utils.FormatUtil;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.logging.log4j.Level;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.earth2me.essentials.I18n.tl;

public class DiscordSettings implements IConf {
    private final EssentialsConfiguration config;
    private final EssentialsDiscord plugin;

    private final Map<String, Long> nameToChannelIdMap = new HashMap<>();
    private final Map<Long, List<String>> channelIdToNamesMap = new HashMap<>();

    private OnlineStatus status;
    private Activity statusActivity;

    private Pattern discordFilter;

    private MessageFormat consoleFormat;
    private Level consoleLogLevel;

    private MessageFormat discordToMcFormat;
    private MessageFormat tempMuteFormat;
    private MessageFormat tempMuteReasonFormat;
    private MessageFormat permMuteFormat;
    private MessageFormat permMuteReasonFormat;
    private MessageFormat unmuteFormat;
    private MessageFormat kickFormat;

    public DiscordSettings(EssentialsDiscord plugin) {
        this.plugin = plugin;
        this.config = new EssentialsConfiguration(new File(plugin.getDataFolder(), "config.yml"), "/config.yml", EssentialsDiscord.class);
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
        return config.getList("permit-formatting-roles", String.class);
    }

    public OnlineStatus getStatus() {
        return status;
    }

    public Activity getStatusActivity() {
        return statusActivity;
    }

    public boolean isAlwaysReceivePrimary() {
        return config.getBoolean("always-receive-primary", false);
    }

    public int getChatDiscordMaxLength() {
        return config.getInt("chat.discord-max-length", 2000);
    }

    public boolean isChatFilterNewlines() {
        return config.getBoolean("chat.filter-newlines", true);
    }

    public Pattern getDiscordFilter() {
        return discordFilter;
    }

    public boolean isShowWebhookMessages() {
        return config.getBoolean("chat.show-webhook-messages", false);
    }

    public boolean isShowBotMessages() {
        return config.getBoolean("chat.show-bot-messages", false);
    }

    public boolean isShowAllChat() {
        return config.getBoolean("chat.show-all-chat", false);
    }

    public List<String> getRelayToConsoleList() {
        return config.getList("chat.relay-to-console", String.class);
    }

    public String getConsoleChannelDef() {
        return config.getString("console.channel", "none");
    }

    public MessageFormat getConsoleFormat() {
        return consoleFormat;
    }

    public String getConsoleWebhookName() {
        return config.getString("console.webhook-name", "EssentialsX Console Relay");
    }

    public boolean isConsoleCommandRelay() {
        return config.getBoolean("console.command-relay", false);
    }

    public boolean isConsoleBotCommandRelay() {
        return config.getBoolean("console.bot-command-relay", false);
    }

    public Level getConsoleLogLevel() {
        return consoleLogLevel;
    }

    public boolean isShowAvatar() {
        return config.getBoolean("show-avatar", false);
    }

    public boolean isShowName() {
        return config.getBoolean("show-name", false);
    }

    public boolean isShowDisplayName() {
        return config.getBoolean("show-displayname", false);
    }

    public String getAvatarURL() {
        return config.getString("avatar-url", "https://crafthead.net/helm/{uuid}");
    }

    public boolean isVanishFakeJoinLeave() {
        return config.getBoolean("vanish-fake-join-leave", true);
    }

    public boolean isVanishHideMessages() {
        return config.getBoolean("vanish-hide-messages", true);
    }

    // General command settings

    public boolean isCommandEnabled(String command) {
        return config.getBoolean("commands." + command + ".enabled", true);
    }

    public boolean isCommandEphemeral(String command) {
        return config.getBoolean("commands." + command + ".hide-command", true);
    }

    public List<String> getCommandSnowflakes(String command) {
        return config.getList("commands." + command + ".allowed-roles", String.class);
    }

    public List<String> getCommandAdminSnowflakes(String command) {
        return config.getList("commands." + command + ".admin-roles", String.class);
    }

    // Message formats

    public MessageFormat getDiscordToMcFormat() {
        return discordToMcFormat;
    }

    public MessageFormat getMcToDiscordFormat(Player player) {
        final String format = getFormatString("mc-to-discord");
        final String filled;
        if (plugin.isPAPI() && format != null) {
            filled = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
        } else {
            filled = format;
        }
        return generateMessageFormat(filled, "{displayname}: {message}", false,
                "username", "displayname", "message", "world", "prefix", "suffix");
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

    public MessageFormat getJoinFormat(Player player) {
        final String format = getFormatString("join");
        final String filled;
        if (plugin.isPAPI() && format != null) {
            filled = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
        } else {
            filled = format;
        }
        return generateMessageFormat(filled, ":arrow_right: {displayname} has joined!", false,
                "username", "displayname", "joinmessage", "online", "unique");
    }

    public MessageFormat getQuitFormat(Player player) {
        final String format = getFormatString("quit");
        final String filled;
        if (plugin.isPAPI() && format != null) {
            filled = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
        } else {
            filled = format;
        }
        return generateMessageFormat(filled, ":arrow_left: {displayname} has left!", false,
                "username", "displayname", "quitmessage", "online", "unique");
    }

    public MessageFormat getDeathFormat(Player player) {
        final String format = getFormatString("death");
        final String filled;
        if (plugin.isPAPI() && format != null) {
            filled = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
        } else {
            filled = format;
        }
        return generateMessageFormat(filled, ":skull: {deathmessage}", false,
                "username", "displayname", "deathmessage");
    }

    public MessageFormat getAfkFormat(Player player) {
        final String format = getFormatString("afk");
        final String filled;
        if (plugin.isPAPI() && format != null) {
            filled = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
        } else {
            filled = format;
        }
        return generateMessageFormat(filled, ":person_walking: {displayname} is now AFK!", false,
                "username", "displayname");
    }

    public MessageFormat getUnAfkFormat(Player player) {
        final String format = getFormatString("un-afk");
        final String filled;
        if (plugin.isPAPI() && format != null) {
            filled = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
        } else {
            filled = format;
        }
        return generateMessageFormat(filled, ":keyboard: {displayname} is no longer AFK!", false,
                "username", "displayname");
    }

    public MessageFormat getAdvancementFormat(Player player) {
        final String format = getFormatString("advancement");
        final String filled;
        if (plugin.isPAPI() && format != null) {
            filled = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
        } else {
            filled = format;
        }
        return generateMessageFormat(filled, ":medal: {displayname} has completed the advancement **{advancement}**!", false,
                "username", "displayname", "advancement");
    }

    public String getStartMessage() {
        return config.getString("messages.server-start", ":white_check_mark: The server has started!");
    }

    public String getStopMessage() {
        return config.getString("messages.server-stop", ":octagonal_sign: The server has stopped!");
    }

    public MessageFormat getKickFormat() {
        return kickFormat;
    }

    private String getFormatString(String node) {
        final String pathPrefix = node.startsWith(".") ? "" : "messages.";
        return config.getString(pathPrefix + (pathPrefix.isEmpty() ? node.substring(1) : node), null);
    }

    private MessageFormat generateMessageFormat(String content, String defaultStr, boolean format, String... arguments) {
        content = content == null ? defaultStr : content;
        content = format ? FormatUtil.replaceFormat(content) : FormatUtil.stripFormat(content);
        content = content.replace("'", "''");
        for (int i = 0; i < arguments.length; i++) {
            content = content.replace("{" + arguments[i] + "}", "{" + i + "}");
            content = content.replace("{" + arguments[i].toUpperCase() + "}", "{" + i + "}");
        }
        content = content.replaceAll("\\{([^0-9]+)}", "'{$1}'");
        return new MessageFormat(content);
    }

    @Override
    public void reloadConfig() {
        if (plugin.isInvalidStartup()) {
            plugin.getLogger().warning(tl("discordReloadInvalid"));
            return;
        }

        config.load();

        // Build channel maps
        nameToChannelIdMap.clear();
        channelIdToNamesMap.clear();
        final Map<String, Object> section = ConfigurateUtil.getRawMap(config, "channels");
        for (Map.Entry<String, Object> entry : section.entrySet()) {
            if (entry.getValue() instanceof Long) {
                final long value = (long) entry.getValue();
                nameToChannelIdMap.put(entry.getKey(), value);
                channelIdToNamesMap.computeIfAbsent(value, o -> new ArrayList<>()).add(entry.getKey());
            }
        }

        // Presence stuff
        status = OnlineStatus.fromKey(config.getString("presence.status", "online"));
        if (status == OnlineStatus.UNKNOWN) {
            // Default invalid status to online
            status = OnlineStatus.ONLINE;
        }

        final String activity = config.getString("presence.activity", "default").trim().toUpperCase().replace("CUSTOM_STATUS", "DEFAULT");
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
            statusActivity = Activity.of(activityType, config.getString("presence.message", "Minecraft"));
        }

        final String filter = config.getString("chat.discord-filter", null);
        if (filter != null && !filter.trim().isEmpty()) {
            try {
                discordFilter = Pattern.compile(filter);
            } catch (PatternSyntaxException e) {
                plugin.getLogger().log(java.util.logging.Level.WARNING, "Invalid pattern for \"chat.discord-filter\": " + e.getMessage());
                discordFilter = null;
            }
        } else {
            discordFilter = null;
        }

        consoleLogLevel = Level.toLevel(config.getString("console.log-level", null), Level.INFO);

        consoleFormat = generateMessageFormat(getFormatString(".console.format"), "[{timestamp} {level}] {message}", false,
                "timestamp", "level", "message");

        discordToMcFormat = generateMessageFormat(getFormatString("discord-to-mc"), "&6[#{channel}] &3{fullname}&7: &f{message}", true,
                "channel", "username", "discriminator", "fullname", "nickname", "color", "message", "role");
        unmuteFormat = generateMessageFormat(getFormatString("unmute"), "{displayname} unmuted.", false, "username", "displayname");
        tempMuteFormat = generateMessageFormat(getFormatString("temporary-mute"), "{controllerdisplayname} has muted player {displayname} for {time}.", false,
                "username", "displayname", "controllername", "controllerdisplayname", "time");
        permMuteFormat = generateMessageFormat(getFormatString("permanent-mute"), "{controllerdisplayname} permanently muted {displayname}.", false,
                "username", "displayname", "controllername", "controllerdisplayname");
        tempMuteReasonFormat = generateMessageFormat(getFormatString("temporary-mute-reason"), "{controllerdisplayname} has muted player {displayname} for {time}. Reason: {reason}.", false,
                "username", "displayname", "controllername", "controllerdisplayname", "time", "reason");
        permMuteReasonFormat = generateMessageFormat(getFormatString("permanent-mute-reason"), "{controllerdisplayname} has muted player {displayname}. Reason: {reason}.", false,
                "username", "displayname", "controllername", "controllerdisplayname", "reason");
        kickFormat = generateMessageFormat(getFormatString("kick"), "{displayname} was kicked with reason: {reason}", false,
                "username", "displayname", "reason");

        plugin.onReload();
    }
}
