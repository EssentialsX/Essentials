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
			final String ipAddress = u.getLastLoginAddress();
			if (ipAddress.length() == 0)
			{
				throw new Exception(Util.i18n("playerNotFound"));
			}
			ess.getServer().banIP(u.getLastLoginAddress());
			sender.sendMessage(Util.i18n("banIpAddress"));
		}
	}
}
