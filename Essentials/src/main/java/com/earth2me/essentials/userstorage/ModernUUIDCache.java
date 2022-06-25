package com.earth2me.essentials.userstorage;

import com.earth2me.essentials.utils.StringUtil;
import com.google.common.io.Files;
import net.ess3.api.IEssentials;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class ModernUUIDCache {
    private final IEssentials ess;

    /**
     * We use a name to uuid map for offline caching due to the following scenario:
     * * JRoy and mdcfeYT420 play on a server
     * * mdcfeYT420 changes his name to mdcfe
     * * mdcfe doesn't log in the server for 31 days
     * * JRoy changes his name to mdcfeYT420
     * * mdcfeYT420 (previously JRoy) logs in the server
     * In a UUID->Name based map, multiple uuids now point to the same map
     * preventing any command which allows for offline players to resolve a
     * single uuid from a name.
     *
     * This map is backed by a file-based cache. If this cache is missing, a new
     * one is populated by iterating over all files in the userdata folder and
     * caching the {@code last-account-name} value.
     */
    private final ConcurrentHashMap<String, UUID> nameToUuidMap = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<UUID> uuidCache = new CopyOnWriteArraySet<>();

    private final ScheduledExecutorService writeExecutor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean pendingNameWrite = new AtomicBoolean(false);
    private final AtomicBoolean pendingUuidWrite = new AtomicBoolean(false);
    private final File nameToUuidFile;
    private final File uuidCacheFile;

    public ModernUUIDCache(final IEssentials ess) {
        this.ess = ess;
        this.nameToUuidFile = new File(ess.getDataFolder(), "usermap.bin");
        this.uuidCacheFile = new File(ess.getDataFolder(), "uuids.bin");
        loadCache();
        writeExecutor.scheduleWithFixedDelay(() -> {
            if (pendingNameWrite.compareAndSet(true, false)) {
                saveNameToUuidCache();
            }

            if (pendingUuidWrite.compareAndSet(true, false)) {
                saveUuidCache();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public UUID getCachedUUID(final String name) {
        return nameToUuidMap.get(getSanitizedName(name));
    }

    public Set<UUID> getCachedUUIDs() {
        return Collections.unmodifiableSet(uuidCache);
    }

    public Map<String, UUID> getNameCache() {
        return Collections.unmodifiableMap(nameToUuidMap);
    }

    public int getCacheSize() {
        return uuidCache.size();
    }

    public String getSanitizedName(final String name) {
        return ess.getSettings().isSafeUsermap() ? StringUtil.safeString(name) : name;
    }

    public void updateCache(final UUID uuid, final String name) {
        if (uuidCache.add(uuid)) {
            pendingUuidWrite.set(true);
        }
        if (name != null) {
            final UUID replacedUuid = nameToUuidMap.put(getSanitizedName(name), uuid);
            if (!uuid.equals(replacedUuid)) {
                pendingNameWrite.set(true);
            }
        }
    }

    public void removeCache(final UUID uuid) {
        if (uuid == null) {
            return;
        }

        if (uuidCache.remove(uuid)) {
            pendingUuidWrite.set(true);
        }

        final Set<String> toRemove = new HashSet<>();
        for (final Map.Entry<String, UUID> entry : nameToUuidMap.entrySet()) {
            if (uuid.equals(entry.getValue())) {
                toRemove.add(entry.getKey());
            }
        }

        for (final String name : toRemove) {
            nameToUuidMap.remove(name);
        }

        if (!toRemove.isEmpty()) {
            pendingNameWrite.set(true);
        }
    }

    private void loadCache() {
        try {
            if (!nameToUuidFile.exists()) {
                if (!nameToUuidFile.createNewFile()) {
                    throw new RuntimeException("Error while creating usermap.bin");
                }
                return;
            }

            if (ess.getSettings().isDebug()) {
                ess.getLogger().log(Level.INFO, "Loading Name->UUID cache from disk...");
            }

            nameToUuidMap.clear();

            try (final DataInputStream dis = new DataInputStream(new FileInputStream(nameToUuidFile))) {
                while (dis.available() > 0) {
                    nameToUuidMap.put(dis.readUTF(), new UUID(dis.readLong(), dis.readLong()));
                }
            }
        } catch (IOException e) {
            ess.getLogger().log(Level.SEVERE, "Error while loading Name->UUID cache", e);
        }

        try {
            if (!uuidCacheFile.exists()) {
                if (!uuidCacheFile.createNewFile()) {
                    throw new RuntimeException("Error while creating uuids.bin");
                }
                return;
            }

            if (ess.getSettings().isDebug()) {
                ess.getLogger().log(Level.INFO, "Loading UUID cache from disk...");
            }

            uuidCache.clear();

            try (final DataInputStream dis = new DataInputStream(new FileInputStream(uuidCacheFile))) {
                while (dis.available() > 0) {
                    uuidCache.add(new UUID(dis.readLong(), dis.readLong()));
                }
            }
        } catch (IOException e) {
            ess.getLogger().log(Level.SEVERE, "Error while loading UUID cache", e);
        }
    }

    private void saveUuidCache() {
        if (ess.getSettings().isDebug()) {
            ess.getLogger().log(Level.INFO, "Saving UUID cache to disk...");
        }

        try {
            final File tmpMap = File.createTempFile("uuids", ".tmp.bin", ess.getDataFolder());

            writeUuidCache(tmpMap, uuidCache);
            //noinspection UnstableApiUsage
            Files.move(tmpMap, uuidCacheFile);
        } catch (IOException e) {
            ess.getLogger().log(Level.SEVERE, "Error while saving UUID cache", e);
        }
    }

    private void saveNameToUuidCache() {
        if (ess.getSettings().isDebug()) {
            ess.getLogger().log(Level.INFO, "Saving Name->UUID cache to disk...");
        }

        try {
            final File tmpMap = File.createTempFile("usermap", ".tmp.bin", ess.getDataFolder());

            writeNameUuidMap(tmpMap, nameToUuidMap);
            //noinspection UnstableApiUsage
            Files.move(tmpMap, nameToUuidFile);
        } catch (IOException e) {
            ess.getLogger().log(Level.SEVERE, "Error while saving Name->UUID cache", e);
        }
    }

    public void blockingSave() {
        saveUuidCache();
        saveNameToUuidCache();
    }

    public static void writeUuidCache(final File file, Set<UUID> uuids) throws IOException {
        try (final DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            for (final UUID uuid: uuids) {
                dos.writeLong(uuid.getMostSignificantBits());
                dos.writeLong(uuid.getLeastSignificantBits());
            }
        }
    }

    public static void writeNameUuidMap(final File file, final Map<String, UUID> nameToUuidMap) throws IOException {
        try (final DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            for (final Map.Entry<String, UUID> entry : nameToUuidMap.entrySet()) {
                dos.writeUTF(entry.getKey());
                final UUID uuid = entry.getValue();
                dos.writeLong(uuid.getMostSignificantBits());
                dos.writeLong(uuid.getLeastSignificantBits());
            }
        }
    }

    public void shutdown() {
        writeExecutor.shutdownNow();
        saveUuidCache();
        saveNameToUuidCache();
    }
}
