package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Backup implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private transient final Server server;
	private transient final IEssentials ess;
	private transient boolean running = false;
	private transient int taskId = -1;
	private transient boolean active = false;

	public Backup(final IEssentials ess)
	{
		this.ess = ess;
		server = ess.getServer();
		if (server.getOnlinePlayers().length > 0)
		{
			startTask();
		}
	}

	void onPlayerJoin()
	{
		startTask();
	}

	private void startTask()
	{
		if (!running)
		{
			final long interval = ess.getSettings().getBackupInterval() * 1200; // minutes -> ticks
			if (interval < 1200)
			{
				return;
			}
			taskId = ess.scheduleSyncRepeatingTask(this, interval, interval);
			running = true;
		}
	}

	@Override
	public void run()
	{
		if (active)
		{
			return;
		}
		active = true;
		final String command = ess.getSettings().getBackupCommand();
		if (command == null || "".equals(command))
		{
			return;
		}
		if ("save-all".equalsIgnoreCase(command)) {
			final CommandSender cs = server.getConsoleSender();
			server.dispatchCommand(cs, "save-all");
			active = false;
			return;
		}
		LOGGER.log(Level.INFO, _("backupStarted"));
		final CommandSender cs = server.getConsoleSender();
		server.dispatchCommand(cs, "save-all");
		server.dispatchCommand(cs, "save-off");

		ess.scheduleAsyncDelayedTask(
				new Runnable()
				{
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
							ess.scheduleSyncDelayedTask(
									new Runnable()
									{
										@Override
										public void run()
										{
											server.dispatchCommand(cs, "save-on");
											if (server.getOnlinePlayers().length == 0)
											{
												running = false;
												if (taskId != -1)
												{
													server.getScheduler().cancelTask(taskId);
												}
											}
											active = false;
											LOGGER.log(Level.INFO, _("backupFinished"));
										}
									});
						}
					}
				});
	}
}
