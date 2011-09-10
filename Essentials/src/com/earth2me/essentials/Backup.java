package com.earth2me.essentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;


public class Backup implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private transient final CraftServer server;
	private transient final IEssentials ess;
	private transient boolean running = false;
	private transient int taskId = -1;
	private transient boolean active = false;

	public Backup(final IEssentials ess)
	{
		this.ess = ess;
		server = (CraftServer)ess.getServer();
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
		LOGGER.log(Level.INFO, Util.i18n("backupStarted"));
		final CommandSender cs = server.getServer().console;
		server.dispatchCommand(cs, "save-all");
		server.dispatchCommand(cs, "save-off");

		ess.scheduleAsyncDelayedTask(
				new Runnable()
				{
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
											LOGGER.log(Level.INFO, Util.i18n("backupFinished"));
										}
									});
						}
					}
				});
	}
}
