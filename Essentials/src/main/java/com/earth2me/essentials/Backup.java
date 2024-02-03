package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

public class Backup implements Runnable {
    private transient final Server server;
    private transient final IEssentials ess;
    private final AtomicBoolean pendingShutdown = new AtomicBoolean(false);
    private transient boolean running = false;
    private transient int taskId = -1;
    private transient boolean active = false;
    private transient CompletableFuture<Object> taskLock = null;

    public Backup(final IEssentials ess) {
        this.ess = ess;
        server = ess.getServer();
        if (!ess.getOnlinePlayers().isEmpty() || ess.getSettings().isAlwaysRunBackup()) {
            ess.runTaskAsynchronously(this::startTask);
        }
    }

    public void onPlayerJoin() {
        startTask();
    }

    public synchronized void stopTask() {
        running = false;
        if (taskId != -1) {
            server.getScheduler().cancelTask(taskId);
        }
        taskId = -1;
    }

    private synchronized void startTask() {
        if (!running) {
            final long interval = ess.getSettings().getBackupInterval() * 1200; // minutes -> ticks
            if (interval < 1200) {
                return;
            }
            taskId = ess.scheduleSyncRepeatingTask(this, interval, interval);
            running = true;
        }
    }

    public CompletableFuture<Object> getTaskLock() {
        return taskLock;
    }

    public void setPendingShutdown(final boolean shutdown) {
        pendingShutdown.set(shutdown);
    }

    @Override
    public void run() {
        if (active) {
            return;
        }
        final String command = ess.getSettings().getBackupCommand();
        if (command == null || "".equals(command)) {
            return;
        }
        active = true;
        taskLock = new CompletableFuture<>();
        if ("save-all".equalsIgnoreCase(command)) {
            final CommandSender cs = server.getConsoleSender();
            server.dispatchCommand(cs, "save-all");
            active = false;
            taskLock.complete(new Object());
            return;
        }
        ess.getLogger().log(Level.INFO, tlLiteral("backupStarted"));
        final CommandSender cs = server.getConsoleSender();
        server.dispatchCommand(cs, "save-all");
        server.dispatchCommand(cs, "save-off");

        ess.runTaskAsynchronously(() -> {
            try {
                final ProcessBuilder childBuilder = new ProcessBuilder(command.split(" "));
                childBuilder.redirectErrorStream(true);
                childBuilder.directory(ess.getDataFolder().getParentFile().getParentFile());
                final Process child = childBuilder.start();
                ess.runTaskAsynchronously(() -> {
                    try {
                        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()))) {
                            String line;
                            do {
                                line = reader.readLine();
                                if (line != null) {
                                    ess.getLogger().log(Level.INFO, line);
                                }
                            } while (line != null);
                        }
                    } catch (final IOException ex) {
                        ess.getLogger().log(Level.SEVERE, "An error occurred while reading backup child process", ex);
                    }
                });
                child.waitFor();
            } catch (final InterruptedException | IOException ex) {
                ess.getLogger().log(Level.SEVERE, "An error occurred while building the backup child process", ex);
            } finally {
                class BackupEnableSaveTask implements Runnable {
                    @Override
                    public void run() {
                        server.dispatchCommand(cs, "save-on");
                        if (!ess.getSettings().isAlwaysRunBackup() && ess.getOnlinePlayers().isEmpty()) {
                            stopTask();
                        }
                        active = false;
                        taskLock.complete(new Object());
                        ess.getLogger().log(Level.INFO, tlLiteral("backupFinished"));
                    }
                }

                if (!pendingShutdown.get()) {
                    ess.scheduleSyncDelayedTask(new BackupEnableSaveTask());
                }
            }
        });
    }
}
