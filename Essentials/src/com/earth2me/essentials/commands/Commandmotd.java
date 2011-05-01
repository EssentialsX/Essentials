package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandmotd extends EssentialsCommand
{
	public Commandmotd()
	{
		super("motd");
	}
	
	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		charge(sender);
		for (String m : ess.getMotd(sender, "§cThere is no message of the day."))
		{
			sender.sendMessage(m);
		}
	}
}
