package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandkill extends EssentialsCommand
{
	public Commandkill()
	{
		super("kill");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		charge(sender);
		for (Player p : server.matchPlayer(args[0]))
		{
			p.setHealth(0);
			sender.sendMessage("§cKilled " + p.getDisplayName() + ".");
		}
	}
}
