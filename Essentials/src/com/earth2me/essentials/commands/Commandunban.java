package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandunban extends EssentialsCommand
{
	public Commandunban()
	{
		super("unban");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		try
		{
			final User player = getPlayer(server, args, 0, true);
			player.setBanned(false);
			sender.sendMessage(Util.i18n("unbannedPlayer"));
		}
		catch (NoSuchFieldException e)
		{
			throw new Exception(Util.i18n("playerNotFound"));
		}
	}
}
