package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandbanip extends EssentialsCommand
{
	public Commandbanip()
	{
		super("banip");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final User u = ess.getUser(args[0]);

		if (u == null)
		{
			ess.getServer().banIP(args[0]);
			sender.sendMessage(Util.i18n("banIpAddress"));
		}
		else
		{
			ess.getServer().banIP(u.getAddress().getAddress().getHostAddress());
			sender.sendMessage(Util.i18n("banIpAddress"));
		}
	}
}
