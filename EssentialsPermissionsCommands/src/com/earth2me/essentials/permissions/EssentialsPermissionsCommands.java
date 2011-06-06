package com.earth2me.essentials.permissions;

import com.earth2me.essentials.Essentials;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsPermissionsCommands extends JavaPlugin
{
	@Override
	public void onEnable()
	{
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
	{
		return Essentials.getStatic().onCommandEssentials(sender, command, label, args, EssentialsPermissionsCommands.class.getClassLoader(), "com.earth2me.essentials.permissions.Command");
	}

	@Override
	public void onDisable()
	{
	}
}
