package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;


public class Commandsethome extends EssentialsCommand
{
	public Commandsethome()
	{
		super("sethome");
	}
	
	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		user.setHome(args.length > 0 && args[0].equalsIgnoreCase("default"));
		charge(user);
		user.sendMessage("§7Home set.");
	}
}
