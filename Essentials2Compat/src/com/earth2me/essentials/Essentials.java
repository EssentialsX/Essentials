package com.earth2me.essentials;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class Essentials extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		Bukkit.getLogger().info("You can remove this compatibility plugin, when all plugins are updated to Essentials 3");
		//TODO: Update files to new 3.0 format
		//TODO: Move Eco Api here
	}

	@Override
	public void onDisable()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
