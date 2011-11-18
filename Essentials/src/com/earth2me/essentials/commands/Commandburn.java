package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
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
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		for (Player p : server.matchPlayer(args[0]))
		{
			p.setFireTicks(Integer.parseInt(args[1]) * 20);
			sender.sendMessage(Util.format("burnMsg", p.getDisplayName(), Integer.parseInt(args[1])));
		}
	}
}
