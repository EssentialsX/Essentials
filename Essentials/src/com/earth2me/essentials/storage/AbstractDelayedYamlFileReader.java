package com.earth2me.essentials.storage;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.IEssentials;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;


public abstract class AbstractDelayedYamlFileReader<T extends StorageObject> implements Runnable
{
	private final transient Class<T> clazz;
	private final transient Plugin plugin;
	private final transient ReentrantLock lock = new ReentrantLock();

	public AbstractDelayedYamlFileReader(final IEssentials ess, final Class<T> clazz)
	{
		this.clazz = clazz;
		this.plugin = ess;
	}

	public void schedule(boolean instant)
	{
		if (instant || ((Essentials)plugin).testing)
		{
			run();
		}
		else
		{
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this);
		}
	}

	public abstract File onStart() throws IOException;

	@Override
	public void run()
	{
		lock.lock();
		try
		{
			final File file = onStart();
			try
			{
				final FileReader reader = new FileReader(file);
				try
				{
					final T object = new YamlStorageReader(reader, plugin).load(clazz);
					onSuccess(object);
				}
				finally
				{
					try
					{
						reader.close();
					}
					catch (IOException ex)
					{
						Bukkit.getLogger().log(Level.SEVERE, "File can't be closed: " + file.toString(), ex);
					}
				}

			}
			catch (FileNotFoundException ex)
			{
				onException(ex);
				Bukkit.getLogger().log(Level.INFO, "File not found: {0}", file.toString());
			}
			catch (ObjectLoadException ex)
			{
				onException(ex);
				File broken = new File(file.getAbsolutePath() + ".broken." + System.currentTimeMillis());
				file.renameTo(broken);
				Bukkit.getLogger().log(Level.SEVERE, "The file " + file.toString() + " is broken, it has been renamed to " + broken.toString(), ex.getCause());
			}
		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, "File could not be opened: " + ex.getMessage(), ex);
		}
		finally
		{
			lock.unlock();
		}
	}

	public abstract void onSuccess(T object);

	public abstract void onException(Exception exception);
}
