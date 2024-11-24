package net.essentialsx.discordlink;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class AccountStorage {
    private final Gson gson = new Gson();
    private final EssentialsDiscordLink plugin;
    private final File accountFile;
    private final BiMap<String, String> uuidToDiscordIdMap;
    private final AtomicBoolean mapDirty = new AtomicBoolean(false);
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public AccountStorage(final EssentialsDiscordLink plugin) throws IOException {
        this.plugin = plugin;
        this.accountFile = new File(plugin.getDataFolder(), "accounts.json");
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            throw new IOException("Unable to create account file!");
        }
        if (!accountFile.exists() && !accountFile.createNewFile()) {
            throw new IOException("Unable to create account file!");
        }
        try (final Reader reader = new FileReader(accountFile)) {
            final Map<String, String> map = gson.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType());
            uuidToDiscordIdMap = map == null ? Maps.synchronizedBiMap(HashBiMap.create()) : Maps.synchronizedBiMap(HashBiMap.create(map));
        }

        executorService.scheduleWithFixedDelay(() -> {
            if (!mapDirty.compareAndSet(true, false)) {
                return;
            }

            if (plugin.getEss().getSettings().isDebug()) {
                plugin.getLogger().log(Level.INFO, "Saving linked discord accounts to disk...");
            }

            final Map<String, String> clone;
            clone = new HashMap<>(uuidToDiscordIdMap);
            try (final Writer writer = new FileWriter(accountFile)) {
                gson.toJson(clone, writer);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save link accounts!", e);
                mapDirty.set(true); // mark the map as dirty and pray it fixes itself :D
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public BiMap<String, String> getRawStorageMap() {
        return HashBiMap.create(uuidToDiscordIdMap);
    }

    public void add(final UUID uuid, final String discordId) {
        uuidToDiscordIdMap.forcePut(uuid.toString(), discordId);
        queueSave();
    }

    public boolean remove(final UUID uuid) {
        if (uuidToDiscordIdMap.remove(uuid.toString()) != null) {
            queueSave();
            return true;
        }
        return false;
    }

    public boolean remove(final String discordId) {
        if (uuidToDiscordIdMap.values().removeIf(discordId::equals)) {
            queueSave();
            return true;
        }
        return false;
    }

    public UUID getUUID(final String discordId) {
        final String uuid = uuidToDiscordIdMap.inverse().get(discordId);
        return uuid == null ? null : UUID.fromString(uuid);
    }

    public String getDiscordId(final UUID uuid) {
        return uuidToDiscordIdMap.get(uuid.toString());
    }

    public void queueSave() {
        mapDirty.set(true);
    }

    public void shutdown() {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                plugin.getLogger().log(Level.SEVERE, "Timed out while saving!");
                executorService.shutdownNow();
            }
            if (mapDirty.get()) {
                try (final Writer writer = new FileWriter(accountFile)) {
                    gson.toJson(uuidToDiscordIdMap, writer);
                }
            }
        } catch (InterruptedException | IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to shutdown link accounts save!", e);
            executorService.shutdownNow();
        }
    }
}
