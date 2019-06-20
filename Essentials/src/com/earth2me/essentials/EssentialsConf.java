package com.earth2me.essentials;

import com.google.common.io.Files;
import net.ess3.api.InvalidWorldException;
import org.bukkit.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>EssentialsConf class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class EssentialsConf extends YamlConfiguration {
    /** Constant <code>LOGGER</code> */
    protected static final Logger LOGGER = Logger.getLogger("Essentials");
    protected final File configFile;
    protected String templateName = null;
    /** Constant <code>UTF8</code> */
    protected static final Charset UTF8 = Charset.forName("UTF-8");
    private Class<?> resourceClass = EssentialsConf.class;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private final AtomicInteger pendingDiskWrites = new AtomicInteger(0);
    private final AtomicBoolean transaction = new AtomicBoolean(false);

    /**
     * <p>Constructor for EssentialsConf.</p>
     *
     * @param configFile a {@link java.io.File} object.
     */
    public EssentialsConf(final File configFile) {
        super();
        this.configFile = configFile.getAbsoluteFile();
    }

    private final byte[] bytebuffer = new byte[1024];

    /**
     * <p>load.</p>
     */
    public synchronized void load() {
        if (pendingDiskWrites.get() != 0) {
            LOGGER.log(Level.INFO, "File {0} not read, because it''s not yet written to disk.", configFile);
            return;
        }
        if (!configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                LOGGER.log(Level.SEVERE, tl("failedToCreateConfig", configFile.toString()));
            }
        }
        // This will delete files where the first character is 0. In most cases they are broken.
        if (configFile.exists() && configFile.length() != 0) {
            try {
                final InputStream input = new FileInputStream(configFile);
                try {
                    if (input.read() == 0) {
                        input.close();
                        configFile.delete();
                    }
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        input.close();
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
            } catch (FileNotFoundException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        if (!configFile.exists()) {
            if (legacyFileExists()) {
                convertLegacyFile();
            } else if (altFileExists()) {
                convertAltFile();
            } else if (templateName != null) {
                LOGGER.log(Level.INFO, tl("creatingConfigFromTemplate", configFile.toString()));
                createFromTemplate();
            } else {
                return;
            }
        }


        try {
            try (FileInputStream inputStream = new FileInputStream(configFile)) {
                long startSize = configFile.length();
                if (startSize > Integer.MAX_VALUE) {
                    throw new InvalidConfigurationException("File too big");
                }
                ByteBuffer buffer = ByteBuffer.allocate((int) startSize);
                int length;
                while ((length = inputStream.read(bytebuffer)) != -1) {
                    if (length > buffer.remaining()) {
                        ByteBuffer resize = ByteBuffer.allocate(buffer.capacity() + length - buffer.remaining());
                        int resizePosition = buffer.position();
                        buffer.rewind();
                        resize.put(buffer);
                        resize.position(resizePosition);
                        buffer = resize;
                    }
                    buffer.put(bytebuffer, 0, length);
                }
                buffer.rewind();
                final CharBuffer data = CharBuffer.allocate(buffer.capacity());
                CharsetDecoder decoder = UTF8.newDecoder();
                CoderResult result = decoder.decode(buffer, data, true);
                if (result.isError()) {
                    buffer.rewind();
                    data.clear();
                    LOGGER.log(Level.INFO, "File " + configFile.getAbsolutePath() + " is not utf-8 encoded, trying " + Charset.defaultCharset().displayName());
                    decoder = Charset.defaultCharset().newDecoder();
                    result = decoder.decode(buffer, data, true);
                    if (result.isError()) {
                        throw new InvalidConfigurationException("Invalid Characters in file " + configFile.getAbsolutePath());
                    } else {
                        decoder.flush(data);
                    }
                } else {
                    decoder.flush(data);
                }
                final int end = data.position();
                data.rewind();
                super.loadFromString(data.subSequence(0, end).toString());
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (InvalidConfigurationException ex) {
            File broken = new File(configFile.getAbsolutePath() + ".broken." + System.currentTimeMillis());
            configFile.renameTo(broken);
            LOGGER.log(Level.SEVERE, "The file " + configFile.toString() + " is broken, it has been renamed to " + broken.toString(), ex.getCause());
        }
    }

    /**
     * <p>legacyFileExists.</p>
     *
     * @return a boolean.
     */
    public boolean legacyFileExists() {
        return false;
    }

    /**
     * <p>convertLegacyFile.</p>
     */
    public void convertLegacyFile() {
        LOGGER.log(Level.SEVERE, "Unable to import legacy config file.");
    }

    /**
     * <p>altFileExists.</p>
     *
     * @return a boolean.
     */
    public boolean altFileExists() {
        return false;
    }

    /**
     * <p>convertAltFile.</p>
     */
    public void convertAltFile() {
        LOGGER.log(Level.SEVERE, "Unable to import alt config file.");
    }

    private void createFromTemplate() {
        InputStream istr = null;
        OutputStream ostr = null;
        try {
            istr = resourceClass.getResourceAsStream(templateName);
            if (istr == null) {
                LOGGER.log(Level.SEVERE, tl("couldNotFindTemplate", templateName));
                return;
            }
            ostr = new FileOutputStream(configFile);
            byte[] buffer = new byte[1024];
            int length = 0;
            length = istr.read(buffer);
            while (length > 0) {
                ostr.write(buffer, 0, length);
                length = istr.read(buffer);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, tl("failedToWriteConfig", configFile.toString()), ex);
        } finally {
            try {
                if (istr != null) {
                    istr.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(EssentialsConf.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (ostr != null) {
                    ostr.close();
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, tl("failedToCloseConfig", configFile.toString()), ex);
            }
        }
    }

    /**
     * <p>Setter for the field <code>templateName</code>.</p>
     *
     * @param templateName a {@link java.lang.String} object.
     */
    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    /**
     * <p>getFile.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public File getFile() {
        return configFile;
    }

    /**
     * <p>Setter for the field <code>templateName</code>.</p>
     *
     * @param templateName a {@link java.lang.String} object.
     * @param resClass a {@link java.lang.Class} object.
     */
    public void setTemplateName(final String templateName, final Class<?> resClass) {
        this.templateName = templateName;
        this.resourceClass = resClass;
    }

    /**
     * <p>startTransaction.</p>
     */
    public void startTransaction() {
        transaction.set(true);
    }

    /**
     * <p>stopTransaction.</p>
     */
    public void stopTransaction() {
        transaction.set(false);
        save();
    }

    /**
     * <p>save.</p>
     */
    public void save() {
        try {
            save(configFile);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * <p>saveWithError.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void saveWithError() throws IOException {
        save(configFile);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void save(final File file) throws IOException {
        if (!transaction.get()) {
            delayedSave(file);
        }
    }

    //This may be aborted if there are stagnant requests sitting in queue.
    //This needs fixed to discard outstanding save requests.
    /**
     * <p>forceSave.</p>
     */
    public synchronized void forceSave() {
        try {
            Future<?> future = delayedSave(configFile);
            if (future != null) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * <p>cleanup.</p>
     */
    public synchronized void cleanup() {
        forceSave();
    }

    private Future<?> delayedSave(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        final String data = saveToString();

        if (data.length() == 0) {
            return null;
        }

        pendingDiskWrites.incrementAndGet();

        return EXECUTOR_SERVICE.submit(new WriteRunner(configFile, data, pendingDiskWrites));
    }


    private static class WriteRunner implements Runnable {
        private final File configFile;
        private final String data;
        private final AtomicInteger pendingDiskWrites;

        private WriteRunner(final File configFile, final String data, final AtomicInteger pendingDiskWrites) {
            this.configFile = configFile;
            this.data = data;
            this.pendingDiskWrites = pendingDiskWrites;
        }

        @Override
        public void run() {
            //long startTime = System.nanoTime();
            synchronized (configFile) {
                if (pendingDiskWrites.get() > 1) {
                    // Writes can be skipped, because they are stored in a queue (in the executor).
                    // Only the last is actually written.
                    pendingDiskWrites.decrementAndGet();
                    //LOGGER.log(Level.INFO, configFile + " skipped writing in " + (System.nanoTime() - startTime) + " nsec.");
                    return;
                }
                try {
                    Files.createParentDirs(configFile);

                    if (!configFile.exists()) {
                        try {
                            LOGGER.log(Level.INFO, tl("creatingEmptyConfig", configFile.toString()));
                            if (!configFile.createNewFile()) {
                                LOGGER.log(Level.SEVERE, tl("failedToCreateConfig", configFile.toString()));
                                return;
                            }
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, tl("failedToCreateConfig", configFile.toString()), ex);
                            return;
                        }
                    }

                    try (FileOutputStream fos = new FileOutputStream(configFile)) {
                        try (OutputStreamWriter writer = new OutputStreamWriter(fos, UTF8)) {
                            writer.write(data);
                        }
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                } finally {
                    //LOGGER.log(Level.INFO, configFile + " written to disk in " + (System.nanoTime() - startTime) + " nsec.");
                    pendingDiskWrites.decrementAndGet();
                }
            }
        }
    }

    /**
     * <p>hasProperty.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean hasProperty(final String path) {
        return isSet(path);
    }

    /**
     * <p>getLocation.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @param server a {@link org.bukkit.Server} object.
     * @return a {@link org.bukkit.Location} object.
     * @throws net.ess3.api.InvalidWorldException if any.
     */
    public Location getLocation(final String path, final Server server) throws InvalidWorldException {
        final String worldString = (path == null ? "" : path + ".") + "world";
        final String worldName = getString(worldString);
        if (worldName == null || worldName.isEmpty()) {
            return null;
        }
        final World world = server.getWorld(worldName);
        if (world == null) {
            throw new InvalidWorldException(worldName);
        }
        return new Location(world, getDouble((path == null ? "" : path + ".") + "x", 0), getDouble((path == null ? "" : path + ".") + "y", 0), getDouble((path == null ? "" : path + ".") + "z", 0), (float) getDouble((path == null ? "" : path + ".") + "yaw", 0), (float) getDouble((path == null ? "" : path + ".") + "pitch", 0));
    }

    /**
     * <p>setProperty.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @param loc a {@link org.bukkit.Location} object.
     */
    public void setProperty(final String path, final Location loc) {
        set((path == null ? "" : path + ".") + "world", loc.getWorld().getName());
        set((path == null ? "" : path + ".") + "x", loc.getX());
        set((path == null ? "" : path + ".") + "y", loc.getY());
        set((path == null ? "" : path + ".") + "z", loc.getZ());
        set((path == null ? "" : path + ".") + "yaw", loc.getYaw());
        set((path == null ? "" : path + ".") + "pitch", loc.getPitch());
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack getItemStack(final String path) {
        final ItemStack stack = new ItemStack(Material.valueOf(getString(path + ".type", "AIR")), getInt(path + ".amount", 1), (short) getInt(path + ".damage", 0));
        final ConfigurationSection enchants = getConfigurationSection(path + ".enchant");
        if (enchants != null) {
            for (String enchant : enchants.getKeys(false)) {
                final Enchantment enchantment = Enchantment.getByName(enchant.toUpperCase(Locale.ENGLISH));
                if (enchantment == null) {
                    continue;
                }
                final int level = getInt(path + ".enchant." + enchant, enchantment.getStartLevel());
                stack.addUnsafeEnchantment(enchantment, level);
            }
        }
        return stack;
        /*
         * ,
		 * (byte)getInt(path + ".data", 0)
		 */
    }

    /**
     * <p>setProperty.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @param stack a {@link org.bukkit.inventory.ItemStack} object.
     */
    public void setProperty(final String path, final ItemStack stack) {
        final Map<String, Object> map = new HashMap<>();
        map.put("type", stack.getType().toString());
        map.put("amount", stack.getAmount());
        map.put("damage", stack.getDurability());
        Map<Enchantment, Integer> enchantments = stack.getEnchantments();
        if (!enchantments.isEmpty()) {
            Map<String, Integer> enchant = new HashMap<>();
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                enchant.put(entry.getKey().getName().toLowerCase(Locale.ENGLISH), entry.getValue());
            }
            map.put("enchant", enchant);
        }
        // getData().getData() is broken
        //map.put("data", stack.getDurability());
        set(path, map);
    }

    /**
     * <p>setProperty.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @param object a {@link java.util.List} object.
     */
    public void setProperty(String path, List object) {
        set(path, new ArrayList(object));
    }

    /**
     * <p>setProperty.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @param object a {@link java.util.Map} object.
     */
    public void setProperty(String path, Map object) {
        set(path, new LinkedHashMap(object));
    }

    /**
     * <p>getProperty.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object getProperty(String path) {
        return get(path);
    }

    /**
     * <p>setProperty.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @param bigDecimal a {@link java.math.BigDecimal} object.
     */
    public void setProperty(final String path, final BigDecimal bigDecimal) {
        set(path, bigDecimal.toString());
    }

    /**
     * <p>setProperty.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @param object a {@link java.lang.Object} object.
     */
    public void setProperty(String path, Object object) {
        set(path, object);
    }

    /**
     * <p>removeProperty.</p>
     *
     * @param path a {@link java.lang.String} object.
     */
    public void removeProperty(String path) {
        set(path, null);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Object get(String path) {
        return super.get(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Object get(String path, Object def) {
        return super.get(path, def);
    }

    /**
     * <p>getBigDecimal.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @param def a {@link java.math.BigDecimal} object.
     * @return a {@link java.math.BigDecimal} object.
     */
    public synchronized BigDecimal getBigDecimal(final String path, final BigDecimal def) {
        final String input = super.getString(path);
        return toBigDecimal(input, def);
    }

    /**
     * <p>toBigDecimal.</p>
     *
     * @param input a {@link java.lang.String} object.
     * @param def a {@link java.math.BigDecimal} object.
     * @return a {@link java.math.BigDecimal} object.
     */
    public static BigDecimal toBigDecimal(final String input, final BigDecimal def) {
        if (input == null || input.isEmpty()) {
            return def;
        } else {
            try {
                return new BigDecimal(input, MathContext.DECIMAL128);
            } catch (NumberFormatException e) {
                return def;
            } catch (ArithmeticException e) {
                return def;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean getBoolean(String path) {
        return super.getBoolean(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean getBoolean(String path, boolean def) {
        return super.getBoolean(path, def);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<Boolean> getBooleanList(String path) {
        return super.getBooleanList(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<Byte> getByteList(String path) {
        return super.getByteList(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<Character> getCharacterList(String path) {
        return super.getCharacterList(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized ConfigurationSection getConfigurationSection(String path) {
        return super.getConfigurationSection(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized double getDouble(String path) {
        return super.getDouble(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized double getDouble(final String path, final double def) {
        return super.getDouble(path, def);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<Double> getDoubleList(String path) {
        return super.getDoubleList(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<Float> getFloatList(String path) {
        return super.getFloatList(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized int getInt(String path) {
        return super.getInt(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized int getInt(String path, int def) {
        return super.getInt(path, def);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<Integer> getIntegerList(String path) {
        return super.getIntegerList(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized ItemStack getItemStack(String path, ItemStack def) {
        return super.getItemStack(path, def);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Set<String> getKeys(boolean deep) {
        return super.getKeys(deep);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<?> getList(String path) {
        return super.getList(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<?> getList(String path, List<?> def) {
        return super.getList(path, def);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized long getLong(String path) {
        return super.getLong(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized long getLong(final String path, final long def) {
        return super.getLong(path, def);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<Long> getLongList(String path) {
        return super.getLongList(path);
    }

    /**
     * <p>getMap.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public synchronized Map<String, Object> getMap() {
        return map;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<Map<?, ?>> getMapList(String path) {
        return super.getMapList(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized OfflinePlayer getOfflinePlayer(String path) {
        return super.getOfflinePlayer(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def) {
        return super.getOfflinePlayer(path, def);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<Short> getShortList(String path) {
        return super.getShortList(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String getString(String path) {
        return super.getString(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String getString(String path, String def) {
        return super.getString(path, def);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<String> getStringList(String path) {
        return super.getStringList(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Map<String, Object> getValues(boolean deep) {
        return super.getValues(deep);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Vector getVector(String path) {
        return super.getVector(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Vector getVector(String path, Vector def) {
        return super.getVector(path, def);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isBoolean(String path) {
        return super.isBoolean(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isConfigurationSection(String path) {
        return super.isConfigurationSection(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isDouble(String path) {
        return super.isDouble(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isInt(String path) {
        return super.isInt(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isItemStack(String path) {
        return super.isItemStack(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isList(String path) {
        return super.isList(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isLong(String path) {
        return super.isLong(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isOfflinePlayer(String path) {
        return super.isOfflinePlayer(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isSet(String path) {
        return super.isSet(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isString(String path) {
        return super.isString(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isVector(String path) {
        return super.isVector(path);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void set(String path, Object value) {
        super.set(path, value);
    }
}
