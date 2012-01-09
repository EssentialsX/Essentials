package com.earth2me.essentials.storage;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.IEssentials;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;


public abstract class AbstractDelayedYamlFileWriter implements Runnable
{
	private final transient Plugin plugin;
	private final transient ReentrantLock lock = new ReentrantLock();

	public AbstractDelayedYamlFileWriter(final IEssentials ess)
	{
		this.plugin = ess;
	}

	public void schedule()
	{
		if (((Essentials)plugin).testing)
		{
			run();
		}
		else
		{
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this);
		}
	}

	public abstract File getFile() throws IOException;

	public abstract StorageObject getObject();

	@Override
	public void run()
	{
		lock.lock();
		try
		{
			final File file = getFile();
			PrintWriter pw = null;
			try
			{
				final StorageObject object = getObject();
				final File folder = file.getParentFile();
				if (!folder.exists())
				{
					folder.mkdirs();
				}
				pw = new PrintWriter(file);
				new YamlStorageWriter(pw).save(object);
			}
			catch (FileNotFoundException ex)
			{
				Bukkit.getLogger().log(Level.SEVERE, file.toString(), ex);
			}
			finally
			{
				onFinish();
				if (pw != null)
				{
					pw.close();
				}
			}
		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}
		finally
		{
			lock.unlock();
		}
	}

	public abstract void onFinish();
}
