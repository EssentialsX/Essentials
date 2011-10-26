package com.earth2me.essentials.update;

import com.earth2me.essentials.update.tasks.InstallModule;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;


public class UpdatesDownloader extends WorkListener implements Runnable
{
	public UpdatesDownloader(final Plugin plugin, final VersionInfo newVersionInfo)
	{
		super(plugin, newVersionInfo);
	}

	public void start()
	{
		iterator = getNewVersionInfo().getModules().entrySet().iterator();
		Bukkit.getScheduler().scheduleAsyncDelayedTask(getPlugin(), this);
	}
	private transient Iterator<Entry<String, ModuleInfo>> iterator;

	@Override
	public void run()
	{
		if (iterator.hasNext())
		{
			final Entry<String, ModuleInfo> entry = iterator.next();
			final Plugin plugin = Bukkit.getPluginManager().getPlugin(entry.getKey());
			if (plugin == null)
			{
				run();
			}
			else
			{
				new InstallModule(this, entry.getKey()).start();
			}
		}
	}

	@Override
	public void onWorkAbort(final String message)
	{
		Bukkit.getLogger().log(Level.SEVERE, message);
	}

	@Override
	public void onWorkDone(final String message)
	{
		Bukkit.getLogger().log(Level.INFO, message);
		Bukkit.getScheduler().scheduleAsyncDelayedTask(getPlugin(), this);
	}
}
