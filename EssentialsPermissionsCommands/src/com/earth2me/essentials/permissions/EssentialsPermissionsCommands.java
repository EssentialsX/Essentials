package com.earth2me.essentials.permissions;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Util;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsPermissionsCommands extends JavaPlugin
{
	private static PermissionHandler permissionHandler = null;
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private IEssentials ess;

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
		ess = (IEssentials)pluginManager.getPlugin("Essentials");

		if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion())) {
			LOGGER.log(Level.WARNING, Util.i18n("versionMismatchAll"));
		}
		LOGGER.info(Util.format("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), "essentials team"));
		
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
	{
		return ess.onCommandEssentials(sender, command, label, args, EssentialsPermissionsCommands.class.getClassLoader(), "com.earth2me.essentials.permissions.Command", "groupmanager.");
	}

	@Override
	public void onDisable()
	{
	}
}
