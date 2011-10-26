package com.earth2me.essentials.update.tasks;

import com.earth2me.essentials.update.WorkListener;
import org.bukkit.Bukkit;


public class SelfUpdate extends WorkListener implements Task, Runnable
{
	private final transient WorkListener listener;

	public SelfUpdate(final WorkListener listener)
	{
		super(listener.getPlugin(), listener.getNewVersionInfo());
		this.listener = listener;
	}

	@Override
	public void onWorkAbort(final String message)
	{
		listener.onWorkAbort(message);
	}

	@Override
	public void onWorkDone(final String message)
	{
		listener.onWorkDone(message);
		Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				Bukkit.getServer().reload();
			}
		});
	}

	@Override
	public void start()
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), this);
	}

	@Override
	public void run()
	{
		Bukkit.getScheduler().scheduleAsyncDelayedTask(getPlugin(), new Runnable() {

			@Override
			public void run()
			{
				new InstallModule(SelfUpdate.this, "EssentialsUpdate").start();
			}
		});
	}
	
	
}
