package com.earth2me.essentials;

import net.ess3.api.IEssentials;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class I18n implements net.ess3.api.II18n {
    private static final String MESSAGES = "messages";
    private static final Pattern NODOUBLEMARK = Pattern.compile("''");
    private static final ResourceBundle NULL_BUNDLE = new ResourceBundle() {
        public Enumeration<String> getKeys() {
            return null;
        }

        protected Object handleGetObject(final String key) {
            return null;
        }
    };
    private static I18n instance;
    private final transient Locale defaultLocale = Locale.getDefault();
    private final transient ResourceBundle defaultBundle;
    private final transient IEssentials ess;
    private transient Locale currentLocale = defaultLocale;
    private transient ResourceBundle customBundle;
    private transient ResourceBundle localeBundle;
    private transient Map<String, MessageFormat> messageFormatCache = new HashMap<>();

    public I18n(final IEssentials ess) {
        this.ess = ess;
        defaultBundle = ResourceBundle.getBundle(MESSAGES, Locale.ENGLISH, new UTF8PropertiesControl());
        localeBundle = defaultBundle;
        customBundle = NULL_BUNDLE;
    }

    public static String tl(final String string, final Object... objects) {
        if (instance == null) {
            return "";
        }
        if (objects.length == 0) {
            return NODOUBLEMARK.matcher(instance.translate(string)).replaceAll("'");
        } else {
            return instance.format(string, objects);
        }
    }

    public static String capitalCase(final String input) {
        return input == null || input.length() == 0 ? input : input.toUpperCase(Locale.ENGLISH).charAt(0) + input.toLowerCase(Locale.ENGLISH).substring(1);
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

    private String translate(final String string) {
        try {
            try {
                return customBundle.getString(string);
            } catch (final MissingResourceException ex) {
                return localeBundle.getString(string);
            }
        } catch (final MissingResourceException ex) {
            if (ess == null || ess.getSettings().isDebug()) {
                Logger.getLogger("Essentials").log(Level.WARNING, String.format("Missing translation key \"%s\" in translation file %s", ex.getKey(), localeBundle.getLocale().toString()), ex);
            }
            return defaultBundle.getString(string);
        }
    }

    public String format(final String string, final Object... objects) {
        String format = translate(string);
        MessageFormat messageFormat = messageFormatCache.get(format);
        if (messageFormat == null) {
            try {
                messageFormat = new MessageFormat(format);
            } catch (final IllegalArgumentException e) {
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
            final String[] parts = loc.split("[_\\.]");
            if (parts.length == 1) {
                currentLocale = new Locale(parts[0]);
            }
            if (parts.length == 2) {
                currentLocale = new Locale(parts[0], parts[1]);
            }
            if (parts.length == 3) {
                currentLocale = new Locale(parts[0], parts[1], parts[2]);
            }
        }
        ResourceBundle.clearCache();
        messageFormatCache = new HashMap<>();
        Logger.getLogger("Essentials").log(Level.INFO, String.format("Using locale %s", currentLocale.toString()));

        try {
            localeBundle = ResourceBundle.getBundle(MESSAGES, currentLocale, new UTF8PropertiesControl());
        } catch (final MissingResourceException ex) {
            localeBundle = NULL_BUNDLE;
        }

        try {
            customBundle = ResourceBundle.getBundle(MESSAGES, currentLocale, new FileResClassLoader(I18n.class.getClassLoader(), ess), new UTF8PropertiesControl());
        } catch (final MissingResourceException ex) {
            customBundle = NULL_BUNDLE;
        }
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
                } catch (final MalformedURLException ignored) {
                }
            }
            return null;
        }

        @Override
        public InputStream getResourceAsStream(final String string) {
            final File file = new File(dataFolder, string);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (final FileNotFoundException ignored) {
                }
            }
            return null;
        }
    }

    /**
     * Reads .properties files as UTF-8 instead of ISO-8859-1, which is the default on Java 8/below.
     * Java 9 fixes this by defaulting to UTF-8 for .properties files.
     */
    private static class UTF8PropertiesControl extends ResourceBundle.Control {
        public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload) throws IOException {
            final String resourceName = toResourceName(toBundleName(baseName, locale), "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                final URL url = loader.getResource(resourceName);
                if (url != null) {
                    final URLConnection connection = url.openConnection();
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
