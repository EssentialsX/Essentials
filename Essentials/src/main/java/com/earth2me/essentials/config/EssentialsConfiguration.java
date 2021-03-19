package com.earth2me.essentials.config;

import net.ess3.api.InvalidWorldException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ParsingException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    public void setProperty(final String path, final Location location) {
        //noinspection ConstantConditions
        setInternal(path + ".world", location.getWorld().getName());
        setInternal(path + ".x", location.getX());
        setInternal(path + ".y", location.getY());
        setInternal(path + ".z", location.getZ());
        setInternal(path + ".yaw", location.getYaw());
        setInternal(path + ".pitch", location.getPitch());
    }

    public Location getLocation(final String path) throws InvalidWorldException {
        final String worldName = getString(path + ".world", null);
        if (worldName == null || worldName.isEmpty()) {
            return null;
        }

        final World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new InvalidWorldException(worldName);
        }
        return new Location(world, getDouble(path + ".x", 0), getDouble(path + ".y", 0),
                getDouble(path + ".z", 0), getFloat(path + ".yaw", 0), getFloat(path + ".pitch", 0));
    }

    public void setProperty(final String path, final List<?> list) {
        setInternal(path, list);
    }

    public <T> List<T> getList(final String path, Class<T> type) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) {
            return new ArrayList<>();
        }
        try {
            final List<T> list = node.getList(type);
            if (list == null) {
                return new ArrayList<>();
            }
            return list;
        } catch (SerializationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public boolean isList(String path) {
        final CommentedConfigurationNode node = getInternal(path);
        return node != null && node.isList();
    }

    public void setProperty(final String path, final String value) {
        setInternal(path, value);
    }

    public String getString(final String path, final String def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) {
            return def;
        }
        return node.getString();
    }

    public void setProperty(final String path, final boolean value) {
        setInternal(path, value);
    }

    public boolean getBoolean(final String path, final boolean def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) {
            return def;
        }
        return node.getBoolean();
    }

    public boolean isBoolean(final String path) {
        final CommentedConfigurationNode node = getInternal(path);
        return node != null && node.raw() instanceof Boolean;
    }

    public void setProperty(final String path, final long value) {
        setInternal(path, value);
    }

    public long getLong(final String path, final long def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) {
            return def;
        }
        return node.getLong();
    }

    public void setProperty(final String path, final int value) {
        setInternal(path, value);
    }

    public int getInt(final String path, final int def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) {
            return def;
        }
        return node.getInt();
    }

    public void setProperty(final String path, final double value) {
        setInternal(path, value);
    }

    public double getDouble(final String path, final double def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) {
            return def;
        }
        return node.getDouble();
    }

    public void setProperty(final String path, final float value) {
        setInternal(path, value);
    }

    public float getFloat(final String path, final float def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) {
            return def;
        }
        return node.getFloat();
    }

    public void setProperty(final String path, final BigDecimal value) {
        setProperty(path, value.toString());
    }

    public BigDecimal getBigDecimal(final String path, final BigDecimal def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) {
            return def;
        }
        return ConfigurateUtil.toBigDecimal(node.getString(), def);
    }

    public Object get(final String path) {
        final CommentedConfigurationNode node = getInternal(path);
        return node == null ? null : node.raw();
    }

    public CommentedConfigurationNode getSection(final String path) {
        final CommentedConfigurationNode node = configurationNode.node(toSplitRoot(path));
        if (node.virtual()) {
            return null;
        }
        return node;
    }

    public CommentedConfigurationNode newSection() {
        return loader.createNode();
    }

    public Set<String> getKeys() {
        return ConfigurateUtil.getKeys(configurationNode);
    }

    public void removeProperty(String path) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node != null) {
            try {
                node.set(null);
            } catch (SerializationException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void setInternal(final String path, final Object value) {
        try {
            configurationNode.node(toSplitRoot(path)).set(value);
        } catch (SerializationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private CommentedConfigurationNode getInternal(final String path) {
        final CommentedConfigurationNode node = configurationNode.node(toSplitRoot(path));
        if (node.virtual()) {
            return null;
        }
        return node;
    }

    public boolean hasProperty(final String path) {
        return !configurationNode.node(toSplitRoot(path)).virtual();
    }

    public Object[] toSplitRoot(String node) {
        return node.split("\\.");
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
            delaySave().get();
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
