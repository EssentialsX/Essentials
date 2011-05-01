package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandseen extends EssentialsCommand
{
	public Commandseen()
	{
		super("seen");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		try
		{
			User u = getPlayer(server, args, 0);
			sender.sendMessage("Player " + u.getDisplayName() + " is online since" + Util.formatDateDiff(u.getLastLogin()));
		}
		catch (NoSuchFieldException e)
		{
			User u = ess.getOfflineUser(args[0]);
			if (u == null)
			{
				return;
			}
			sender.sendMessage("Player " + u.getDisplayName() + " is offline since" + Util.formatDateDiff(u.getLastLogout()));
		}
	}
}
