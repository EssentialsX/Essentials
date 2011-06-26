package com.earth2me.essentials.permissions;

import com.earth2me.essentials.Essentials;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsPermissionsCommands extends JavaPlugin
{
	private static PermissionHandler permissionHandler = null;

	public static PermissionHandler getPermissionHandler()
	{
		return permissionHandler;
	}

	@Override
	public void onEnable()
	{
		final PluginManager pluginManager = getServer().getPluginManager();
		final Plugin permissionsPlugin = pluginManager.getPlugin("Permissions");

		if (permissionsPlugin != null
			&& permissionsPlugin.getDescription().getVersion().charAt(0) == '3')
		{
			permissionHandler = ((Permissions)permissionsPlugin).getHandler();
		}
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
	{
		return Essentials.getStatic().onCommandEssentials(sender, command, label, args, EssentialsPermissionsCommands.class.getClassLoader(), "com.earth2me.essentials.permissions.Command", "groupmanager.");
	}

	@Override
	public void onDisable()
	{
	}
}
