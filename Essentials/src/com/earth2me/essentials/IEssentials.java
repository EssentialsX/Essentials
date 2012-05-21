package com.earth2me.essentials;

import com.earth2me.essentials.api.IJails;
import com.earth2me.essentials.metrics.Metrics;
import com.earth2me.essentials.perm.PermissionsHandler;
import com.earth2me.essentials.register.payment.Methods;
import java.util.List;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;


/**
 * @deprecated This will be moved to the api package soon
 */
@Deprecated
public interface IEssentials extends Plugin
{
	void addReloadListener(IConf listener);

	void reload();

	boolean onCommandEssentials(CommandSender sender, Command command, String commandLabel, String[] args, ClassLoader classLoader, String commandPath, String permissionPrefix, IEssentialsModule module);

	User getUser(Object base);

	I18n getI18n();

	User getOfflineUser(String name);

	World getWorld(String name);

	int broadcastMessage(IUser sender, String message);

	ISettings getSettings();

	BukkitScheduler getScheduler();

	IJails getJails();

	Warps getWarps();

	Worth getWorth();

	Backup getBackup();

	Methods getPaymentMethod();

	int scheduleAsyncDelayedTask(Runnable run);

	int scheduleSyncDelayedTask(Runnable run);

	int scheduleSyncDelayedTask(Runnable run, long delay);

	int scheduleSyncRepeatingTask(final Runnable run, long delay, long period);

	TNTExplodeListener getTNTListener();

	PermissionsHandler getPermissionsHandler();

	AlternativeCommandsHandler getAlternativeCommandsHandler();

	void showError(final CommandSender sender, final Throwable exception, final String commandLabel);

	ItemDb getItemDb();

	UserMap getUserMap();
	
	Metrics getMetrics();
	
	void setMetrics(Metrics metrics);
	
	EssentialsTimer getTimer();
	
	List<String> getVanishedPlayers();
}
