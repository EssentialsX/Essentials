package com.earth2me.essentials.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

public class EssentialsUserConfiguration extends EssentialsConfiguration {
    private String username;
    private final UUID uuid;

    public EssentialsUserConfiguration(final String username, final UUID uuid, final File configFile) {
        super(configFile);
        this.username = username;
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    @Override
    public boolean legacyFileExists() {
        return new File(configFile.getParentFile(), username + ".yml").exists();
    }

    @Override
    public void convertLegacyFile() {
        final File file = new File(configFile.getParentFile(), username + ".yml");
        try {
            //noinspection UnstableApiUsage
            Files.move(file, new File(configFile.getParentFile(), uuid + ".yml"));
        } catch (final IOException ex) {
            LOGGER.log(Level.WARNING, "Failed to migrate user: " + username, ex);
        }

        setProperty("last-account-name", username);
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
            //noinspection UnstableApiUsage
            Files.move(getAltFile(), new File(configFile.getParentFile(), uuid + ".yml"));
        } catch (final IOException ex) {
            LOGGER.log(Level.WARNING, "Failed to migrate user: " + username, ex);
        }
    }
}
