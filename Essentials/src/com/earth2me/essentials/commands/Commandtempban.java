package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandtempban extends EssentialsCommand
{
	public Commandtempban()
	{
		super("tempban");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		final User player = getPlayer(server, args, 0, true);
		final String time = getFinalArg(args, 1);
		final long banTimestamp = Util.parseDateDiff(time, true);

		final String banReason = Util.format("tempBanned",  Util.formatDateDiff(banTimestamp));
		player.setBanReason(banReason);
		player.setBanTimeout(banTimestamp);
		player.kickPlayer(banReason);
		ess.getBans().banByName(player.getName());
		server.broadcastMessage(Util.format("playerBanned", player.getName(), banReason));
	}
}
