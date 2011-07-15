package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandban extends EssentialsCommand
{
	public Commandban()
	{
		super("ban");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		if (server.matchPlayer(args[0]).isEmpty())
		{
			ess.getBans().banByName(args[0]);
			server.broadcastMessage(Util.format("playerBanned", args[0], Util.i18n("defaultBanReason")));
		}
		else
		{
			final User player = ess.getUser(server.matchPlayer(args[0]).get(0));
			String banReason;
			if (args.length > 1)
			{
				banReason = getFinalArg(args, 1);
				player.setBanReason(commandLabel);
			}
			else
			{
				banReason = Util.i18n("defaultBanReason");
			}
			player.kickPlayer(banReason);
			ess.getBans().banByName(args[0]);
			server.broadcastMessage(Util.format("playerBanned", player.getName(), banReason));
		}
	}
}
