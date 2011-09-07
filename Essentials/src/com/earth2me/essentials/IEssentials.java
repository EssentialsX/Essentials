package com.earth2me.essentials;

import com.earth2me.essentials.perm.PermissionsHandler;
import com.earth2me.essentials.register.payment.Methods;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;


public interface IEssentials extends Plugin
{
	void addReloadListener(IConf listener);

	void reload();

	boolean onCommandEssentials(CommandSender sender, Command command, String commandLabel, String[] args, ClassLoader classLoader, String commandPath, String permissionPrefix);

	User getUser(Object base);

	User getOfflineUser(String name);

	World getWorld(String name);

	int broadcastMessage(IUser sender, String message);

	ISettings getSettings();

	BukkitScheduler getScheduler();

	String[] getMotd(CommandSender sender, String def);

	String[] getLines(CommandSender sender, String node, String def);

	Jail getJail();

	Warps getWarps();

	Worth getWorth();

	Backup getBackup();

	Spawn getSpawn();

	Methods getPaymentMethod();

	int scheduleAsyncDelayedTask(Runnable run);

	int scheduleSyncDelayedTask(Runnable run);

	int scheduleSyncDelayedTask(Runnable run, long delay);

	int scheduleSyncRepeatingTask(final Runnable run, long delay, long period);

	TNTExplodeListener getTNTListener();

	PermissionsHandler getPermissionsHandler();

	void showError(final CommandSender sender, final Throwable exception, final String commandLabel);

	ItemDb getItemDb();

	UserMap getUserMap();
}
