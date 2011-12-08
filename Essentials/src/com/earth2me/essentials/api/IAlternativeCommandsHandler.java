package com.earth2me.essentials.api;

import java.util.Map;
import org.bukkit.command.PluginCommand;


public interface IAlternativeCommandsHandler
{	
	Map<String, String> disabledCommands();
}
