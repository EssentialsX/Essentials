package com.earth2me.essentials;

import net.ess3.api.IEssentials;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class I18n implements net.ess3.api.II18n {
    private static I18n instance;
    private static final String MESSAGES = "messages";
    private final transient Locale defaultLocale = Locale.getDefault();
    private transient Locale currentLocale = defaultLocale;
    private transient Map<Locale, ResourceBundle> customBundles = new HashMap<>();
    private transient ResourceBundle localeBundle;
    private final transient ResourceBundle defaultBundle;
    private transient Map<String, MessageFormat> messageFormatCache = new HashMap<String, MessageFormat>();
    private final transient IEssentials ess;
    private static final Pattern NODOUBLEMARK = Pattern.compile("''");
    private static final ResourceBundle NULL_BUNDLE = new ResourceBundle() {
        public Enumeration<String> getKeys() {
            return null;
        }

        protected Object handleGetObject(String key) {
            return null;
        }
    };

    public I18n(final IEssentials ess) {
        this.ess = ess;
        defaultBundle = ResourceBundle.getBundle(MESSAGES, Locale.ENGLISH, new UTF8PropertiesControl());
        localeBundle = defaultBundle;
    }

    public void onEnable() {
        instance = this;
    }

    public void onDisable() {
        instance = null;
    }

    @Override
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    private ResourceBundle getBundle(Locale locale) {
        if (customBundles.containsKey(locale)) {
            return customBundles.get(locale);
        }

        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle(MESSAGES, locale, new FileResClassLoader(I18n.class.getClassLoader(), ess), new UTF8PropertiesControl());
        } catch (MissingResourceException ex) {
            try {
                bundle = ResourceBundle.getBundle(MESSAGES, locale, new UTF8PropertiesControl());
            } catch (MissingResourceException ex2) {
                bundle = NULL_BUNDLE;
            }
        }
        customBundles.put(locale, bundle);
        return bundle;
    }

    private String translate(Locale locale, final String string) {
        if (locale == null) {
            locale = currentLocale;
        }
        try {
            try {
                return getBundle(locale).getString(string);
            } catch (MissingResourceException ex) {
                return localeBundle.getString(string);
            }
        } catch (MissingResourceException ex) {
            Logger.getLogger("Essentials").log(Level.WARNING, String.format("Missing translation key \"%s\" in translation file %s", ex.getKey(), localeBundle.getLocale().toString()), ex);
            return defaultBundle.getString(string);
        }
    }

    public static String tl(final String string, final Object... objects) {
        return tl(null, string, objects);
    }

    public static String tl(final Locale locale, final String string, final Object... objects) {
        if (instance == null) {
            return "";
        }
        if (objects.length == 0) {
            return NODOUBLEMARK.matcher(instance.translate(locale, string)).replaceAll("'");
        } else {
            return instance.format(locale, string, objects);
        }
    }

    public String format(final String string, final Object... objects) {
        return format(null, string, objects);
    }

    public String format(final Locale locale, final String string, final Object... objects) {
        String format = translate(locale, string);
        MessageFormat messageFormat = messageFormatCache.get(format);
        if (messageFormat == null) {
            try {
                messageFormat = new MessageFormat(format);
            } catch (IllegalArgumentException e) {
                ess.getLogger().log(Level.SEVERE, "Invalid Translation key for '" + string + "': " + e.getMessage());
                format = format.replaceAll("\\{(\\D*?)\\}", "\\[$1\\]");
                messageFormat = new MessageFormat(format);
            }
            messageFormatCache.put(format, messageFormat);
        }
        return messageFormat.format(objects).replace(' ', ' '); // replace nbsp with a space
    }

    public void updateLocale(final String loc) {
        if (loc != null && !loc.isEmpty()) {
            currentLocale = getLocale(loc);
        }
        ResourceBundle.clearCache();
        messageFormatCache = new HashMap<>();
        Logger.getLogger("Essentials").log(Level.INFO, String.format("Using locale %s", currentLocale.toString()));

        try {
            localeBundle = ResourceBundle.getBundle(MESSAGES, currentLocale, new UTF8PropertiesControl());
        } catch (MissingResourceException ex) {
            localeBundle = NULL_BUNDLE;
        }

        customBundles.clear();
    }

    public static Locale getLocale(String loc) {
        if (loc == null) {
            return instance.currentLocale;
        }
        final String[] parts = loc.split("[_\\.]");
        if (parts.length == 1) {
            return new Locale(parts[0]);
        }
        if (parts.length == 2) {
            return new Locale(parts[0], parts[1]);
        }
        if (parts.length == 3) {
            return new Locale(parts[0], parts[1], parts[2]);
        }
        return instance.currentLocale;
    }

    public static String capitalCase(final String input) {
        return input == null || input.length() == 0 ? input : input.toUpperCase(Locale.ENGLISH).charAt(0) + input.toLowerCase(Locale.ENGLISH).substring(1);
    }

    /**
     * Attempts to load properties files from the plugin directory before falling back to the jar.
     */
    private static class FileResClassLoader extends ClassLoader {
        private final transient File dataFolder;

        FileResClassLoader(final ClassLoader classLoader, final IEssentials ess) {
            super(classLoader);
            this.dataFolder = ess.getDataFolder();
        }

        @Override
        public URL getResource(final String string) {
            final File file = new File(dataFolder, string);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException ignored) {}
            }
            return null;
        }

        @Override
        public InputStream getResourceAsStream(final String string) {
            final File file = new File(dataFolder, string);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException ignored) {}
            }
            return null;
        }
    }

    /**
     * Reads .properties files as UTF-8 instead of ISO-8859-1, which is the default on Java 8/below.
     * Java 9 fixes this by defaulting to UTF-8 for .properties files.
     */
    private static class UTF8PropertiesControl extends ResourceBundle.Control {
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    // use UTF-8 here, this is the important bit
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }
}
