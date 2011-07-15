package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandban extends EssentialsCommand
{
	public Commandban()
	{
		super("ban");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		if (server.matchPlayer(args[0]).isEmpty())
		{
			((CraftServer)server).getHandle().a(args[0]);
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
			((CraftServer)server).getHandle().a(player.getName());
			server.broadcastMessage(Util.format("playerBanned", player.getName(), banReason));
		}
		ess.loadBanList();
	}
}
