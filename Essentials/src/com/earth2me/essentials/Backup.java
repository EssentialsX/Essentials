package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IBackup;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.ISettings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Cleanup;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Backup implements Runnable, IBackup
{
	private static final Logger LOGGER = Bukkit.getLogger();
	private transient final Server server;
	private transient final IEssentials ess;
	private transient final AtomicBoolean running = new AtomicBoolean(false);
	private transient int taskId = -1;
	private transient final AtomicBoolean active = new AtomicBoolean(false);

	public Backup(final IEssentials ess)
	{
		this.ess = ess;
		server = ess.getServer();
		if (server.getOnlinePlayers().length > 0)
		{
			startTask();
		}
	}

	@Override
	public final void startTask()
	{
		if (running.compareAndSet(false, true))
		{
			@Cleanup
			final ISettings settings = ess.getSettings();
			settings.acquireReadLock();
			final long interval = settings.getData().getGeneral().getBackup().getInterval() * 1200; // minutes -> ticks
			if (interval < 1200)
			{
				running.set(false);
				return;
			}
			taskId = ess.scheduleSyncRepeatingTask(this, interval, interval);
		}
	}

	@Override
	public void run()
	{
		if (!active.compareAndSet(false, true))
		{
			return;
		}
		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		final String command = settings.getData().getGeneral().getBackup().getCommand();
		if (command == null || command.isEmpty())
		{
			return;
		}
		LOGGER.log(Level.INFO, _("backupStarted"));
		final CommandSender consoleSender = server.getConsoleSender();
		server.dispatchCommand(consoleSender, "save-all");
		server.dispatchCommand(consoleSender, "save-off");

		ess.scheduleAsyncDelayedTask(new BackupRunner(command));
	}


	private class BackupRunner implements Runnable
	{
		private final transient String command;

		public BackupRunner(final String command)
		{
			this.command = command;
		}

		@Override
		public void run()
		{
			try
			{
				final ProcessBuilder childBuilder = new ProcessBuilder(command);
				childBuilder.redirectErrorStream(true);
				childBuilder.directory(ess.getDataFolder().getParentFile().getParentFile());
				final Process child = childBuilder.start();
				final BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()));
				try
				{
					child.waitFor();
					String line;
					do
					{
						line = reader.readLine();
						if (line != null)
						{
							LOGGER.log(Level.INFO, line);
						}
					}
					while (line != null);
				}
				finally
				{
					reader.close();
				}
			}
			catch (InterruptedException ex)
			{
				LOGGER.log(Level.SEVERE, null, ex);
			}
			catch (IOException ex)
			{
				LOGGER.log(Level.SEVERE, null, ex);
			}
			finally
			{
				ess.scheduleSyncDelayedTask(new EnableSavingRunner());
			}
		}
	}


	private class EnableSavingRunner implements Runnable
	{
		@Override
		public void run()
		{
			server.dispatchCommand(server.getConsoleSender(), "save-on");
			if (server.getOnlinePlayers().length == 0)
			{
				running.set(false);
				if (taskId != -1)
				{
					server.getScheduler().cancelTask(taskId);
				}
			}

			active.set(false);
			LOGGER.log(Level.INFO, _("backupFinished"));
		}
	}
}
