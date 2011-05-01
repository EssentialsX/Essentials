package com.earth2me.essentials.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.*;


public interface IEssentialsCommand
{
	String getName();

	void run(Server server, User user, String commandLabel, Command cmd, String[] args)
			throws Exception;

	void run(Server server, CommandSender sender, String commandLabel, Command cmd, String[] args)
			throws Exception;
}
