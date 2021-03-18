package com.earth2me.essentials.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ParsingException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsConfiguration {
    protected static final Logger LOGGER = Logger.getLogger("Essentials");
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private final AtomicInteger pendingWrites = new AtomicInteger(0);
    private final AtomicBoolean transaction = new AtomicBoolean(false);
    protected Class<?> resourceClass = EssentialsConfiguration.class;
    private final File configFile;
    private final YamlConfigurationLoader loader;
    private final String templateName;
    private CommentedConfigurationNode configurationNode;

    public EssentialsConfiguration(final File configFile) {
        this(configFile, null);
    }

    public EssentialsConfiguration(final File configFile, final String templateName) {
        this.configFile = configFile;
        this.loader = YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).indent(2).file(configFile).build();
        this.templateName = templateName;
    }

    public synchronized void load() {
        if (pendingWrites.get() != 0) {
            LOGGER.log(Level.INFO, "Parsing config file {0} has been aborted due to {1} current pending write(s).", new Object[]{configFile, pendingWrites.get()});
            return;
        }

        if (configFile.getParentFile() != null && !configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                LOGGER.log(Level.SEVERE, tl("failedToCreateConfig", configFile.toString()));
                return;
            }
        }

        if (!configFile.exists() && templateName != null) {
            try (final InputStream is = resourceClass.getResourceAsStream(templateName)) {
                Files.copy(is, configFile.toPath());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, tl("failedToWriteConfig", configFile.toString()), e);
            }
        }

        try {
            configurationNode = loader.load();
        } catch (final ParsingException e) {
            final File broken = new File(configFile.getAbsolutePath() + ".broken." + System.currentTimeMillis());
            if (configFile.renameTo(broken)) {
                LOGGER.log(Level.SEVERE, "The file " + configFile.toString() + " is broken, it has been renamed to " + broken.toString(), e.getCause());
                return;
            }
            LOGGER.log(Level.SEVERE, "The file " + configFile.toString() + " is broken. A backup file has failed to be created", e.getCause());
        } catch (final ConfigurateException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            // Something is wrong! We need a node! I hope the backup worked!
            if (configurationNode == null) {
                configurationNode = loader.createNode();
            }
        }
    }

    /**
     * Begins a transaction.
     *
     * A transaction informs Essentials to pause the saving of data. This is should be used when
     * bulk operations are being done and data shouldn't be saved until after the transaction has
     * been completed.
     */
    public void startTransaction() {
        transaction.set(true);
    }

    public void stopTransaction() {
        transaction.set(false);
        save();
    }

    public synchronized void save() {
        if (!transaction.get()) {
            delaySave();
        }
    }

    public synchronized void blockingSave() {
        try {
            final Future<?> future = delaySave();
            if (future != null) {
                future.get();
            }
        } catch (final InterruptedException | ExecutionException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private Future<?> delaySave() {
        final CommentedConfigurationNode node = configurationNode.copy();

        pendingWrites.incrementAndGet();

        return EXECUTOR_SERVICE.submit(new ConfigurationSaveTask(loader, node, pendingWrites));
    }
}
