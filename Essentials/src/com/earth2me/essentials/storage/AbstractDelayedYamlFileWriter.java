package com.earth2me.essentials.storage;

import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;

public abstract class AbstractDelayedYamlFileWriter implements Runnable {
    private final transient File file;

    public AbstractDelayedYamlFileWriter(final IEssentials ess, final File file) {
        this.file = file;
        ess.runTaskAsynchronously(this);
    }

    public abstract StorageObject getObject();

    @Override
    public void run() {
        PrintWriter pw = null;
        try {
            final StorageObject object = getObject();
            final File folder = file.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
            pw = new PrintWriter(file);
            new YamlStorageWriter(pw).save(object);
        } catch (final FileNotFoundException ex) {
            Bukkit.getLogger().log(Level.SEVERE, file.toString(), ex);
        } finally {
            onFinish();
            if (pw != null) {
                pw.close();
            }
        }

    }

    public abstract void onFinish();
}
