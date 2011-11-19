package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commanddeljail extends EssentialsCommand
{
	public Commanddeljail()
	{
		super("deljail");
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		ess.getJail().delJail(args[0]);
		sender.sendMessage(Util.format("deleteJail", args[0]));
	}
}
