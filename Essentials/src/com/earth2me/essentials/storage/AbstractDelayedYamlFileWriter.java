package com.earth2me.essentials.storage;

import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;


/**
 * <p>Abstract AbstractDelayedYamlFileWriter class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public abstract class AbstractDelayedYamlFileWriter implements Runnable {
    private final transient File file;

    /**
     * <p>Constructor for AbstractDelayedYamlFileWriter.</p>
     *
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @param file a {@link java.io.File} object.
     */
    public AbstractDelayedYamlFileWriter(IEssentials ess, File file) {
        this.file = file;
        ess.runTaskAsynchronously(this);
    }

    /**
     * <p>getObject.</p>
     *
     * @return a {@link com.earth2me.essentials.storage.StorageObject} object.
     */
    public abstract StorageObject getObject();

    /** {@inheritDoc} */
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
        } catch (FileNotFoundException ex) {
            Bukkit.getLogger().log(Level.SEVERE, file.toString(), ex);
        } finally {
            onFinish();
            if (pw != null) {
                pw.close();
            }
        }

    }

    /**
     * <p>onFinish.</p>
     */
    public abstract void onFinish();
}
