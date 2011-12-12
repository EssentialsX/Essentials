package com.earth2me.essentials.api;

import com.earth2me.essentials.listener.TNTExplodeListener;
import com.earth2me.essentials.perm.IPermissionsHandler;
import com.earth2me.essentials.register.payment.Methods;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public interface IEssentials extends Plugin
{
	void addReloadListener(IReload listener);

	IUser getUser(Player player);
	
	IUser getUser(String playerName);

	int broadcastMessage(IUser sender, String message);

	II18n getI18n();

	ISettings getSettings();

	IGroups getGroups();

	IJails getJails();

	IWarps getWarps();

	IWorth getWorth();

	IItemDb getItemDb();

	IUserMap getUserMap();

	IBackup getBackup();

	IEssentialsEconomy getEconomy();
	
	ICommandHandler getCommandHandler();

	World getWorld(String name);

	Methods getPaymentMethod();

	int scheduleAsyncDelayedTask(Runnable run);

	int scheduleSyncDelayedTask(Runnable run);

	int scheduleSyncDelayedTask(Runnable run, long delay);

	int scheduleSyncRepeatingTask(Runnable run, long delay, long period);

	IPermissionsHandler getPermissionsHandler();
	
	void reload();

	TNTExplodeListener getTNTListener();
}
