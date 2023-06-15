package com.earth2me.essentials.config;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.config.annotations.DeleteIfIncomplete;
import com.earth2me.essentials.config.annotations.DeleteOnEmpty;
import com.earth2me.essentials.config.entities.CommandCooldown;
import com.earth2me.essentials.config.entities.LazyLocation;
import com.earth2me.essentials.config.processors.DeleteIfIncompleteProcessor;
import com.earth2me.essentials.config.processors.DeleteOnEmptyProcessor;
import com.earth2me.essentials.config.serializers.BigDecimalTypeSerializer;
import com.earth2me.essentials.config.serializers.CommandCooldownSerializer;
import com.earth2me.essentials.config.serializers.LocationTypeSerializer;
import com.earth2me.essentials.config.serializers.MailMessageSerializer;
import com.earth2me.essentials.config.serializers.MaterialTypeSerializer;
import net.ess3.api.InvalidWorldException;
import net.essentialsx.api.v2.services.mail.MailMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.loader.ParsingException;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

public class EssentialsConfiguration {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private static final ObjectMapper.Factory MAPPER_FACTORY = ObjectMapper.factoryBuilder()
            .addProcessor(DeleteOnEmpty.class, (data, value) -> new DeleteOnEmptyProcessor())
            .addProcessor(DeleteIfIncomplete.class, (data, value) -> new DeleteIfIncompleteProcessor())
            .build();
    private static final TypeSerializerCollection SERIALIZERS = TypeSerializerCollection.defaults().childBuilder()
            .registerAnnotatedObjects(MAPPER_FACTORY)
            .register(BigDecimal.class, new BigDecimalTypeSerializer())
            .register(LazyLocation.class, new LocationTypeSerializer())
            .register(Material.class, new MaterialTypeSerializer())
            .register(CommandCooldown.class, new CommandCooldownSerializer())
            .register(MailMessage.class, new MailMessageSerializer())
            .build();

    private final AtomicInteger pendingWrites = new AtomicInteger(0);
    private final AtomicBoolean transaction = new AtomicBoolean(false);
    private Class<?> resourceClass = EssentialsConfiguration.class;
    protected final File configFile;
    private final YamlConfigurationLoader loader;
    private final String templateName;
    private CommentedConfigurationNode configurationNode;
    private Runnable saveHook;

    public EssentialsConfiguration(final File configFile) {
        this(configFile, null);
    }

    public EssentialsConfiguration(final File configFile, final String templateName) {
        this(configFile, templateName, (String) null);
    }

    public EssentialsConfiguration(final File configFile, final String templateName, final Class<?> resourceClass) {
        this(configFile, templateName, (String) null);
        this.resourceClass = resourceClass;
    }

    public EssentialsConfiguration(final File configFile, final String templateName, final String header) {
        this.configFile = configFile;
        this.loader = YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts
                        .header(header)
                        .serializers(SERIALIZERS))
                .headerMode(HeaderMode.PRESET)
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .file(configFile)
                .build();
        this.templateName = templateName;
    }

    public CommentedConfigurationNode getRootNode() {
        return configurationNode;
    }

    public void setRootHolder(final Class<?> holderClass, final Object holder) {
        try {
            getRootNode().set(holderClass, holder);
        } catch (SerializationException e) {
            Essentials.getWrappedLogger().log(Level.SEVERE, "Error while saving user config: " + configFile.getName(), e);
            throw new RuntimeException(e);
        }
    }

    public File getFile() {
        return configFile;
    }

    public void setProperty(String path, final Location location) {
        setInternal(path, LazyLocation.fromLocation(location));
    }

    public LazyLocation getLocation(final String path) throws InvalidWorldException {
        final CommentedConfigurationNode node = path == null ? getRootNode() : getSection(path);
        if (node == null) {
            return null;
        }

        try {
            return node.get(LazyLocation.class);
        } catch (SerializationException e) {
            Essentials.getWrappedLogger().log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public Map<String, LazyLocation> getLocationSectionMap(final String path) {
        final CommentedConfigurationNode node = getSection(path);
        final Map<String, LazyLocation> result = new HashMap<>();
        for (final Map.Entry<String, CommentedConfigurationNode> entry : ConfigurateUtil.getMap(node).entrySet()) {
            final CommentedConfigurationNode jailNode = entry.getValue();
            try {
                result.put(entry.getKey().toLowerCase(Locale.ENGLISH), jailNode.get(LazyLocation.class));
            } catch (SerializationException e) {
                Essentials.getWrappedLogger().log(Level.WARNING, "Error serializing key " + entry.getKey(), e);
            }
        }
        return result;
    }

    public void setProperty(final String path, final List<?> list) {
        setInternal(path, list);
    }

    public <T> void setExplicitList(final String path, final List<T> list, final Type type) {
        try {
            toSplitRoot(path, configurationNode).set(type, list);
        } catch (SerializationException e) {
            Essentials.getWrappedLogger().log(Level.SEVERE, e.getMessage(), e);
        }
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
            Essentials.getWrappedLogger().log(Level.SEVERE, e.getMessage(), e);
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
        setInternal(path, value);
    }

    public BigDecimal getBigDecimal(final String path, final BigDecimal def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) {
            return def;
        }
        try {
            return node.get(BigDecimal.class);
        } catch (SerializationException e) {
            Essentials.getWrappedLogger().log(Level.SEVERE, e.getMessage(), e);
            return def;
        }
    }

    public void setRaw(final String path, final Object value) {
        setInternal(path, value);
    }

    public Object get(final String path) {
        final CommentedConfigurationNode node = getInternal(path);
        return node == null ? null : node.raw();
    }

    public CommentedConfigurationNode getSection(final String path) {
        final CommentedConfigurationNode node = toSplitRoot(path, configurationNode);
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

    public Map<String, CommentedConfigurationNode> getMap() {
        return ConfigurateUtil.getMap(configurationNode);
    }

    public Map<String, String> getStringMap(String path) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null || !node.isMap()) {
            return Collections.emptyMap();
        }

        final Map<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<Object, CommentedConfigurationNode> entry : node.childrenMap().entrySet()) {
            map.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue().rawScalar()));
        }
        return map;
    }

    public void removeProperty(String path) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node != null) {
            try {
                node.set(null);
            } catch (SerializationException e) {
                Essentials.getWrappedLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void setInternal(final String path, final Object value) {
        try {
            toSplitRoot(path, configurationNode).set(value);
        } catch (SerializationException e) {
            Essentials.getWrappedLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private CommentedConfigurationNode getInternal(final String path) {
        final CommentedConfigurationNode node = toSplitRoot(path, configurationNode);
        if (node.virtual()) {
            return null;
        }
        return node;
    }

    public boolean hasProperty(final String path) {
        return !toSplitRoot(path, configurationNode).isNull();
    }

    public CommentedConfigurationNode toSplitRoot(String path, final CommentedConfigurationNode node) {
        if (path == null) {
            return node;
        }
        path = path.startsWith(".") ? path.substring(1) : path;
        return node.node(path.contains(".") ? path.split("\\.") : new Object[]{path});
    }

    public synchronized void load() {
        if (pendingWrites.get() != 0) {
            Essentials.getWrappedLogger().log(Level.INFO, "Parsing config file {0} has been aborted due to {1} current pending write(s).", new Object[]{configFile, pendingWrites.get()});
            return;
        }

        if (configFile.getParentFile() != null && !configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                Essentials.getWrappedLogger().log(Level.SEVERE, tlLiteral("failedToCreateConfig", configFile.toString()));
                return;
            }
        }

        if (!configFile.exists()) {
            if (legacyFileExists()) {
                convertLegacyFile();
            } else if (altFileExists()) {
                convertAltFile();
            } else if (templateName != null) {
                try (final InputStream is = resourceClass.getResourceAsStream(templateName)) {
                    Essentials.getWrappedLogger().log(Level.INFO, tlLiteral("creatingConfigFromTemplate", configFile.toString()));
                    Files.copy(is, configFile.toPath());
                } catch (IOException e) {
                    Essentials.getWrappedLogger().log(Level.SEVERE, tlLiteral("failedToWriteConfig", configFile.toString()), e);
                }
            }
        }

        try {
            configurationNode = loader.load();
        } catch (final ParsingException e) {
            final File broken = new File(configFile.getAbsolutePath() + ".broken." + System.currentTimeMillis());
            if (configFile.renameTo(broken)) {
                Essentials.getWrappedLogger().log(Level.SEVERE, "The file " + configFile + " is broken, it has been renamed to " + broken, e.getCause());
                return;
            }
            Essentials.getWrappedLogger().log(Level.SEVERE, "The file " + configFile + " is broken. A backup file has failed to be created", e.getCause());
        } catch (final ConfigurateException e) {
            Essentials.getWrappedLogger().log(Level.SEVERE, e.getMessage(), e);
        } finally {
            // Something is wrong! We need a node! I hope the backup worked!
            if (configurationNode == null) {
                configurationNode = loader.createNode();
            }
        }
    }

    public boolean legacyFileExists() {
        return false;
    }

    public void convertLegacyFile() {

    }

    public boolean altFileExists() {
        return false;
    }

    public void convertAltFile() {

    }

    /**
     * Begins a transaction.
     * <p>
     * A transaction informs Essentials to pause the saving of data. This is should be used when
     * bulk operations are being done and data shouldn't be saved until after the transaction has
     * been completed.
     */
    public void startTransaction() {
        transaction.set(true);
    }

    public void stopTransaction() {
        stopTransaction(false);
    }

    public void stopTransaction(final boolean blocking) {
        transaction.set(false);
        if (blocking) {
            blockingSave();
        } else {
            save();
        }
    }

    public boolean isTransaction() {
        return transaction.get();
    }

    public void setSaveHook(Runnable saveHook) {
        this.saveHook = saveHook;
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
            Essentials.getWrappedLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private Future<?> delaySave() {
        if (saveHook != null) {
            saveHook.run();
        }

        final CommentedConfigurationNode node = configurationNode.copy();

        pendingWrites.incrementAndGet();

        return EXECUTOR_SERVICE.submit(new ConfigurationSaveTask(loader, node, pendingWrites));
    }
}
