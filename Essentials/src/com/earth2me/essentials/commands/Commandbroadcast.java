package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandbroadcast extends EssentialsCommand
{
	public Commandbroadcast()
	{
		super("broadcast");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		charge(sender);
		server.broadcastMessage(Util.format("broadcast", getFinalArg(args, 0)));
	}
}
