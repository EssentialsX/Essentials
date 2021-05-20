package net.essentialsx.discordlink;

import com.earth2me.essentials.IEssentialsModule;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountStorage implements IEssentialsModule {
    private final static Logger logger = Logger.getLogger("EssentialsDiscordLink");
    private final Gson gson = new Gson();
    private final EssentialsDiscordLink ess;
    private final File accountFile;
    private final ConcurrentHashMap<String, String> uuidToDiscordIdMap = new ConcurrentHashMap<>();
    private final ExecutorService writeService = Executors.newSingleThreadExecutor();

    public AccountStorage(final EssentialsDiscordLink ess) throws IOException {
        this.ess = ess;
        this.accountFile = new File(ess.getDataFolder(), "accounts.json");
        if (!accountFile.exists()) {
            if (!accountFile.getParentFile().exists() && !accountFile.getParentFile().mkdirs() && !accountFile.createNewFile()) {
                throw new IOException("Unable to create account file!");
            }

        }
    }

    public void add(final UUID uuid, final String discordId) {
        synchronized (uuidToDiscordIdMap) {
            uuidToDiscordIdMap.values().removeIf(discordId::equals);
            uuidToDiscordIdMap.put(uuid.toString(), discordId);
            queueSave();
        }
    }

    public void remove(final UUID uuid) {
        synchronized (uuidToDiscordIdMap) {
            uuidToDiscordIdMap.remove(uuid.toString());
            queueSave();
        }
    }

    public void remove(final String discordId) {
        synchronized (uuidToDiscordIdMap) {
            uuidToDiscordIdMap.values().removeIf(discordId::equals);
            queueSave();
        }
    }

    public UUID getUUID(final String discordId) {
        synchronized (uuidToDiscordIdMap) {
            for (Map.Entry<String, String> entry : uuidToDiscordIdMap.entrySet()) {
                if (entry.getValue().equals(discordId)) {
                    return UUID.fromString(entry.getKey());
                }
            }
        }
        return null;
    }

    public String getDiscordId(final UUID uuid) {
        synchronized (uuidToDiscordIdMap) {
            return uuidToDiscordIdMap.get(uuid.toString());
        }
    }

    public void queueSave() {
        synchronized (uuidToDiscordIdMap) {
            final Map<String, String> clone = new HashMap<>(uuidToDiscordIdMap);
            writeService.submit(() -> {
                try {
                    gson.toJson(clone, new FileWriter(accountFile));
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Failed to save link accounts!", e);
                }
            });
        }
    }

    public void shutdown() {
        synchronized (uuidToDiscordIdMap) {
            try {
                if (!writeService.awaitTermination(10, TimeUnit.SECONDS)) {
                    writeService.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Failed to shutdown link accounts save!", e);
                writeService.shutdownNow();
            }
        }
    }
}
