package com.earth2me.essentials.commands;

import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public interface IEssentialsCommand
{
	String getName();

	void run(Server server, IUser user, String commandLabel, Command cmd, String[] args)
			throws Exception;

	void run(Server server, CommandSender sender, String commandLabel, Command cmd, String[] args)
			throws Exception;

	void setEssentials(IEssentials ess);

	void setEssentialsModule(IEssentialsModule module);
}
