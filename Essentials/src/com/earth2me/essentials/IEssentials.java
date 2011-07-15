package com.earth2me.essentials;

import com.earth2me.essentials.register.payment.Methods;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.scheduler.CraftScheduler;
import org.bukkit.plugin.PluginDescriptionFile;


public interface IEssentials
{
	void addReloadListener(IConf listener);

	void reload();

	boolean onCommandEssentials(CommandSender sender, Command command, String commandLabel, String[] args, ClassLoader classLoader, String commandPath, String permissionPrefix);

	User getUser(Object base);

	User getOfflineUser(String name);

	World getWorld(String name);

	int broadcastMessage(String name, String message);

	Settings getSettings();

	CraftScheduler getScheduler();

	String[] getMotd(CommandSender sender, String def);

	String[] getLines(CommandSender sender, String node, String def);

	Jail getJail();

	Warps getWarps();

	Worth getWorth();

	Backup getBackup();

	Spawn getSpawn();

	Methods getPaymentMethod();
	
	Server getServer();

	File getDataFolder();

	PluginDescriptionFile getDescription();

	int scheduleAsyncDelayedTask(Runnable run);

	int scheduleSyncDelayedTask(Runnable run);

	int scheduleSyncDelayedTask(Runnable run, long delay);

	int scheduleSyncRepeatingTask(final Runnable run, long delay, long period);

	BanWorkaround getBans();

	TNTExplodeListener getTNTListener();

	IPermissionsHandler getPermissionsHandler();

	void showError(final CommandSender sender, final Throwable exception, final String commandLabel);

	Map<String, User> getAllUsers();
}
