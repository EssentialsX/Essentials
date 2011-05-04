package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandrules extends EssentialsCommand
{
	public Commandrules()
	{
		super("rules");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		charge(sender);
		for (String m : ess.getLines(sender, "rules", "§cThere are no rules specified yet."))
		{
			sender.sendMessage(m);
		}
	}
}
