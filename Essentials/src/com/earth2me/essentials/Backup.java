package com.earth2me.essentials;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;

public class Backup implements Runnable {
	private static final Logger logger = Logger.getLogger("Minecraft");
	private final CraftServer server;
	private final IEssentials ess;
	private boolean running = false;
	private int taskId = -1;
	private boolean active = false;

	public Backup(IEssentials ess) {
		this.ess = ess;
		server = (CraftServer)ess.getServer();
		if (server.getOnlinePlayers().length > 0) {
			startTask();
		}
	}	

	void onPlayerJoin() {
		startTask();
	}
	
	private void startTask() {
		if (!running) {
			long interval = ess.getSettings().getBackupInterval()*1200; // minutes -> ticks
			if (interval < 1200) {
				return;
			}
			taskId = ess.scheduleSyncRepeatingTask(this, interval, interval);
			running = true;
		}
	}

	public void run() {
		if (active) return;
		active = true;
		final String command = ess.getSettings().getBackupCommand();
		if (command == null || "".equals(command)) {
			return;
		}
		logger.log(Level.INFO, Util.i18n("backupStarted"));
		final CommandSender cs = server.getServer().console;
		server.dispatchCommand(cs, "save-all");
		server.dispatchCommand(cs, "save-off");
		
		ess.scheduleAsyncDelayedTask(
			new Runnable() {

			public void run() {
				try {
					Process child = Runtime.getRuntime().exec(command);
					child.waitFor();
				} catch (InterruptedException ex) {
					logger.log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					logger.log(Level.SEVERE, null, ex);
				} finally {
					ess.scheduleSyncDelayedTask(
						new Runnable() {

						public void run() {
							server.dispatchCommand(cs, "save-on");
							if (server.getOnlinePlayers().length == 0) {
								running = false;
								if (taskId != -1) {
									server.getScheduler().cancelTask(taskId);
								}
							}
							active = false;
							logger.log(Level.INFO, Util.i18n("backupFinished"));
						}
					});
				}
			}
		});
	}

}
