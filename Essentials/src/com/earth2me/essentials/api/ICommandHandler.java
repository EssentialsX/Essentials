package com.earth2me.essentials.api;

import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;


public interface ICommandHandler extends IReload
{
	Map<String, String> disabledCommands();

	public void removePlugin(Plugin plugin);

	public void addPlugin(Plugin plugin);

	boolean handleCommand(CommandSender sender, Command command, String commandLabel, String[] args);

	void showCommandError(CommandSender sender, String commandLabel, Throwable exception);
}
