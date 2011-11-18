package com.earth2me.essentials;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import org.bukkit.Bukkit;


public class I18n
{
	private static I18n instance;
	private static final String MESSAGES = "messages";
	private final transient Locale defaultLocale = Locale.getDefault();
	private transient Locale currentLocale = defaultLocale;
	private transient ResourceBundle customBundle = ResourceBundle.getBundle(MESSAGES, defaultLocale);
	private transient ResourceBundle localeBundle = ResourceBundle.getBundle(MESSAGES, defaultLocale);
	private final transient ResourceBundle defaultBundle = ResourceBundle.getBundle(MESSAGES, Locale.ENGLISH);
	private final transient Map<String, MessageFormat> messageFormatCache = new HashMap<String, MessageFormat>();

	public I18n()
	{
		instance = this;
	}

	public Locale getCurrentLocale()
	{
		return currentLocale;
	}

	public String translate(final String string)
	{
		try
		{
			try
			{
				return customBundle.getString(string);
			}
			catch (MissingResourceException ex)
			{
				return localeBundle.getString(string);
			}
		}
		catch (MissingResourceException ex)
		{
			Bukkit.getLogger().log(Level.WARNING, String.format("Missing translation key \"%s\" in translation file %s", ex.getKey(), localeBundle.getLocale().toString()), ex);
			return defaultBundle.getString(string);
		}
	}

	public static String _(final String string, final Object... objects)
	{
		if (objects.length == 0)
		{
			return instance.translate(string);
		}
		else
		{
			return instance.format(string, objects);
		}
	}

	public String format(final String string, final Object... objects)
	{
		final String format = translate(string);
		MessageFormat messageFormat = messageFormatCache.get(format);
		if (messageFormat == null)
		{
			messageFormat = new MessageFormat(format);
			messageFormatCache.put(format, messageFormat);
		}
		return messageFormat.format(objects);
	}

	public void updateLocale(final String loc, final IEssentials ess)
	{
		if (loc == null || loc.isEmpty())
		{
			return;
		}
		final String[] parts = loc.split("[_\\.]");
		if (parts.length == 1)
		{
			currentLocale = new Locale(parts[0]);
		}
		if (parts.length == 2)
		{
			currentLocale = new Locale(parts[0], parts[1]);
		}
		if (parts.length == 3)
		{
			currentLocale = new Locale(parts[0], parts[1], parts[2]);
		}
		Bukkit.getLogger().log(Level.INFO, String.format("Using locale %s", currentLocale.toString()));
		customBundle = ResourceBundle.getBundle(MESSAGES, currentLocale, new FileResClassLoader(I18n.class.getClassLoader(), ess));
		localeBundle = ResourceBundle.getBundle(MESSAGES, currentLocale);
	}

	public static String lowerCase(final String input)
	{
		return input == null ? null : input.toLowerCase(Locale.ENGLISH);
	}

	public static String capitalCase(final String input)
	{
		return input == null || input.length() == 0 
			   ? input
			   : input.toUpperCase(Locale.ENGLISH).charAt(0)
				 + input.toLowerCase(Locale.ENGLISH).substring(1);
	}


	private static class FileResClassLoader extends ClassLoader
	{
		private final transient File dataFolder;

		public FileResClassLoader(final ClassLoader classLoader, final IEssentials ess)
		{
			super(classLoader);
			this.dataFolder = ess.getDataFolder();
		}

		@Override
		public URL getResource(final String string)
		{
			final File file = new File(dataFolder, string);
			if (file.exists())
			{
				try
				{
					return file.toURI().toURL();
				}
				catch (MalformedURLException ex)
				{
				}
			}
			return super.getResource(string);
		}

		@Override
		public InputStream getResourceAsStream(final String string)
		{
			final File file = new File(dataFolder, string);
			if (file.exists())
			{
				try
				{
					return new FileInputStream(file);
				}
				catch (FileNotFoundException ex)
				{
				}
			}
			return super.getResourceAsStream(string);
		}
	}
}
