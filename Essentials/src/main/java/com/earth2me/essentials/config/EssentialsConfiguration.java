package com.earth2me.essentials.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ParsingException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsConfiguration {
    protected static final Logger LOGGER = Logger.getLogger("Essentials");
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private final AtomicInteger pendingWrites = new AtomicInteger(0);
    private Class<?> resourceClass = EssentialsConfiguration.class;
    private final File configFile;
    private final YamlConfigurationLoader loader;
    private final String templateName;
    private CommentedConfigurationNode configurationNode;

    public EssentialsConfiguration(final File configFile) {
        this(configFile, null);
    }

    public EssentialsConfiguration(final File configFile, final String templateName) {
        this.configFile = configFile;
        this.loader = YamlConfigurationLoader.builder().file(configFile).build();
        this.templateName = templateName;
    }

    public synchronized void load() {
        if (pendingWrites.get() != 0) {
            LOGGER.log(Level.INFO, "Parsing config file {0} has been aborted due to {1} current pending write(s).", new Object[]{configFile, pendingWrites.get()});
            return;
        }

        if (!configFile.getParentFile().exists()) {
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
}
