package com.earth2me.essentials;

import com.google.common.io.Files;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.regex.Pattern;


public class UUIDMap {
    private final transient net.ess3.api.IEssentials ess;
    private File userList;
    private final transient Pattern splitPattern = Pattern.compile(",");
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private final AtomicInteger pendingDiskWrites = new AtomicInteger(0);

    public UUIDMap(final net.ess3.api.IEssentials ess) {
        this.ess = ess;
        userList = new File(ess.getDataFolder(), "usermap.csv");

    }

    public void loadAllUsers(final ConcurrentSkipListMap<String, UUID> names, final ConcurrentSkipListMap<UUID, ArrayList<String>> history) {
        try {
            if (!userList.exists()) {
                userList.createNewFile();
            }

            synchronized (pendingDiskWrites) {
                if (ess.getSettings().isDebug()) {
                    ess.getLogger().log(Level.INFO, "Reading usermap from disk");
                }

                names.clear();
                history.clear();

                try (BufferedReader reader = new BufferedReader(new FileReader(userList))) {
                    while (true) {
                        final String line = reader.readLine();
                        if (line == null) {
                            break;
                        } else {
                            final String[] values = splitPattern.split(line);
                            if (values.length == 2) {
                                final String name = values[0];
                                final UUID uuid = UUID.fromString(values[1]);
                                names.put(name, uuid);
                                if (!history.containsKey(uuid)) {
                                    final ArrayList<String> list = new ArrayList<String>();
                                    list.add(name);
                                    history.put(uuid, list);
                                } else {
                                    final ArrayList<String> list = history.get(uuid);
                                    if (!list.contains(name)) {
                                        list.add(name);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void writeUUIDMap() {
        _writeUUIDMap();
    }

    public void forceWriteUUIDMap() {
        if (ess.getSettings().isDebug()) {
            ess.getLogger().log(Level.INFO, "Forcing usermap write to disk");
        }
        try {
            Future<?> future = _writeUUIDMap();
            if (future != null) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            ess.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public Future<?> _writeUUIDMap() {
        Map<String, UUID> names = ess.getUserMap().getNames();
        if (names.size() < 1) {
            return null;
        }
        // The _names_ Map is being shallowly cloned because of an issue that occurs during restarts/reloads that
        // causes UserMap database to be cleared. The assumed culprit is names.clear() when loading in UserMap. 
        //
        // For more information, please refer to #213.
        names = new HashMap<>(names);
        pendingDiskWrites.incrementAndGet();
        Future<?> future = EXECUTOR_SERVICE.submit(new WriteRunner(ess.getDataFolder(), userList, names, pendingDiskWrites));
        return future;
    }


    private static class WriteRunner implements Runnable {
        private final File location;
        private final File endFile;
        private final Map<String, UUID> names;
        private final AtomicInteger pendingDiskWrites;

        private WriteRunner(final File location, final File endFile, final Map<String, UUID> names, final AtomicInteger pendingDiskWrites) {
            this.location = location;
            this.endFile = endFile;
            this.names = names;
            this.pendingDiskWrites = pendingDiskWrites;
        }

        @Override
        public void run() {
            synchronized (pendingDiskWrites) {
                if (pendingDiskWrites.get() > 1) {
                    pendingDiskWrites.decrementAndGet();
                    return;
                }

                File configFile = null;

                try {
                    configFile = File.createTempFile("usermap", ".tmp.csv", location);

                    final BufferedWriter bWriter = new BufferedWriter(new FileWriter(configFile));
                    for (Map.Entry<String, UUID> entry : names.entrySet()) {
                        bWriter.write(entry.getKey() + "," + entry.getValue().toString());
                        bWriter.newLine();
                    }

                    bWriter.close();
                    Files.move(configFile, endFile);
                } catch (IOException ex) {
                    try {
                        if (configFile != null && configFile.exists()) {
                            Files.move(configFile, new File(endFile.getParentFile(), "usermap.bak.csv"));
                        }
                    } catch (Exception ex2) {
                        Bukkit.getLogger().log(Level.SEVERE, ex2.getMessage(), ex2);
                    }
                    Bukkit.getLogger().log(Level.WARNING, ex.getMessage(), ex);
                } finally {
                    pendingDiskWrites.decrementAndGet();
                }
            }
        }
    }
}
