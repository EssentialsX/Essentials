package net.essentialsx.discord;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;

import java.io.File;

public class DiscordSettings implements IConf {
    private final EssentialsConf config;
    private final EssentialsDiscord plugin;

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
        return config.getLong("channels." + key, 0);
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
        plugin.onReload();
    }
}
