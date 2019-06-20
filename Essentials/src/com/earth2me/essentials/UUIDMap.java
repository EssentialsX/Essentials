package com.earth2me.essentials;

import com.google.common.io.Files;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.regex.Pattern;


/**
 * <p>UUIDMap class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class UUIDMap {
    private final transient net.ess3.api.IEssentials ess;
    private File userList;
    private final transient Pattern splitPattern = Pattern.compile(",");

    private static boolean pendingWrite;
    private static final ScheduledExecutorService writeScheduler = Executors.newScheduledThreadPool(1);
    private final Runnable writeTaskRunnable;

    private static boolean loading = false;

    /**
     * <p>Constructor for UUIDMap.</p>
     *
     * @param ess a {@link net.ess3.api.IEssentials} object.
     */
    public UUIDMap(final net.ess3.api.IEssentials ess) {
        this.ess = ess;
        userList = new File(ess.getDataFolder(), "usermap.csv");
        pendingWrite = false;
        writeTaskRunnable = new Runnable() {
            @Override
            public void run() {
                if (pendingWrite) {
                    try {
                        new WriteRunner(ess.getDataFolder(), userList, ess.getUserMap().getNames()).run();
                    } catch (Throwable t) { // bad code to prevent task from being suppressed
                        t.printStackTrace();
                    }
                }
            }
        };
        writeScheduler.scheduleWithFixedDelay(writeTaskRunnable, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * <p>loadAllUsers.</p>
     *
     * @param names a {@link java.util.concurrent.ConcurrentSkipListMap} object.
     * @param history a {@link java.util.concurrent.ConcurrentSkipListMap} object.
     */
    public void loadAllUsers(final ConcurrentSkipListMap<String, UUID> names, final ConcurrentSkipListMap<UUID, ArrayList<String>> history) {
        try {
            if (!userList.exists()) {
                userList.createNewFile();
            }

            if (ess.getSettings().isDebug()) {
                ess.getLogger().log(Level.INFO, "Reading usermap from disk");
            }

            if (loading) {
                return;
            }

            names.clear();
            history.clear();
            loading = true;

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
                                final ArrayList<String> list = new ArrayList<>();
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
            loading = false;
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * <p>writeUUIDMap.</p>
     */
    public void writeUUIDMap() {
        pendingWrite = true;
    }

    /**
     * <p>forceWriteUUIDMap.</p>
     */
    public void forceWriteUUIDMap() {
        if (ess.getSettings().isDebug()) {
            ess.getLogger().log(Level.INFO, "Forcing usermap write to disk");
        }
        pendingWrite = true;
        writeTaskRunnable.run();
    }

    /**
     * <p>shutdown.</p>
     */
    public void shutdown() {
        writeScheduler.submit(writeTaskRunnable);
        writeScheduler.shutdown();
    }

    private static class WriteRunner implements Runnable {
        private final File location;
        private final File endFile;
        private final Map<String, UUID> names;

        private WriteRunner(final File location, final File endFile, final Map<String, UUID> names) {
            this.location = location;
            this.endFile = endFile;
            this.names = new HashMap<>(names);
        }

        @Override
        public void run() {
            pendingWrite = false;
            if (loading || names.isEmpty()) {
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
            }
        }
    }
}
