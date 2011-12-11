package com.earth2me.essentials.api;

import java.util.Map;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;


public interface IAlternativeCommandsHandler
{	
	Map<String, String> disabledCommands();

	public void removePlugin(Plugin plugin);

	public void addPlugin(Plugin plugin);
}
