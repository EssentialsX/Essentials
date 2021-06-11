package com.earth2me.essentials;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

public class EssentialsUserConf extends EssentialsConf {
    public String username;
    public final UUID uuid;

    public EssentialsUserConf(final String username, final UUID uuid, final File configFile) {
        super(configFile);
        this.username = username;
        this.uuid = uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean legacyFileExists() {
        final File file = new File(configFile.getParentFile(), username + ".yml");
        return file.exists();
    }

    @Override
    public void convertLegacyFile() {
        final File file = new File(configFile.getParentFile(), username + ".yml");
        try {
            Files.move(file, new File(configFile.getParentFile(), uuid + ".yml"));
        } catch (final IOException ex) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to migrate user: " + username, ex);
        }

        setProperty("lastAccountName", username);
    }

    private File getAltFile() {
        final UUID fn = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username.toLowerCase(Locale.ENGLISH)).getBytes(Charsets.UTF_8));
        return new File(configFile.getParentFile(), fn.toString() + ".yml");
    }

    @Override
    public boolean altFileExists() {
        if (username.equals(username.toLowerCase())) {
            return false;
        }
        return getAltFile().exists();
    }

    @Override
    public void convertAltFile() {
        try {
            Files.move(getAltFile(), new File(configFile.getParentFile(), uuid + ".yml"));
        } catch (final IOException ex) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to migrate user: " + username, ex);
        }
    }
}
