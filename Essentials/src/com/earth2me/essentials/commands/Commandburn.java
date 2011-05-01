package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandburn extends EssentialsCommand
{
	public Commandburn()
	{
		super("burn");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		charge(sender);
		for (Player p : server.matchPlayer(args[0]))
		{
			p.setFireTicks(Integer.parseInt(args[1]) * 20);
			sender.sendMessage("§cYou set " + p.getDisplayName() + " on fire for " + Integer.parseInt(args[1]) + "seconds.");
		}
	}
}
