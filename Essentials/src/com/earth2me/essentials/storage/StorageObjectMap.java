package com.earth2me.essentials.storage;

import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.InvalidNameException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import org.bukkit.Bukkit;


public abstract class StorageObjectMap<I> extends CacheLoader<String, I> implements IStorageObjectMap<I>
{
	protected final transient IEssentials ess;
	private final transient File folder;
	protected final transient Cache<String, I> cache = CacheBuilder.newBuilder().softValues().build(this);
	protected final transient ConcurrentSkipListSet<String> keys = new ConcurrentSkipListSet<String>();

	public StorageObjectMap(final IEssentials ess, final String folderName)
	{
		super();
		this.ess = ess;
		this.folder = new File(ess.getDataFolder(), folderName);
		if (!folder.exists())
		{
			folder.mkdirs();
		}
		loadAllObjectsAsync();
	}

	private void loadAllObjectsAsync()
	{
		ess.scheduleAsyncDelayedTask(new Runnable()
		{
			@Override
			public void run()
			{
				if (!folder.exists() || !folder.isDirectory())
				{
					return;
				}
				keys.clear();
				cache.invalidateAll();
				for (String string : folder.list())
				{
					try
					{
						if (!string.endsWith(".yml"))
						{
							continue;
						}
						final String name = Util.decodeFileName(string.substring(0, string.length() - 4));
						keys.add(name.toLowerCase(Locale.ENGLISH));
					}
					catch (InvalidNameException ex)
					{
						Bukkit.getLogger().log(Level.WARNING, "Invalid filename: " + string, ex);
					}
				}
			}
		});
	}

	@Override
	public boolean objectExists(final String name)
	{
		return keys.contains(name.toLowerCase(Locale.ENGLISH));
	}

	@Override
	public I getObject(final String name)
	{
		try
		{
			return (I)cache.get(name.toLowerCase(Locale.ENGLISH));
		}
		catch (ExecutionException ex)
		{
			return null;
		}
		catch (UncheckedExecutionException ex)
		{
			return null;
		}
	}

	@Override
	public abstract I load(final String name) throws Exception;

	@Override
	public void removeObject(final String name) throws InvalidNameException
	{
		keys.remove(name.toLowerCase(Locale.ENGLISH));
		cache.invalidate(name.toLowerCase(Locale.ENGLISH));
		final File file = getStorageFile(name);
		if (file.exists())
		{
			file.delete();
		}
	}

	@Override
	public Set<String> getAllKeys()
	{
		return Collections.unmodifiableSet(keys);
	}

	@Override
	public int getKeySize()
	{
		return keys.size();
	}

	@Override
	public File getStorageFile(final String name) throws InvalidNameException
	{
		if (!folder.exists() || !folder.isDirectory())
		{
			throw new InvalidNameException(new IOException("Folder does not exists: " + folder));
		}
		return new File(folder, Util.sanitizeFileName(name) + ".yml");
	}

	@Override
	public void onReload()
	{
		loadAllObjectsAsync();
	}
}
