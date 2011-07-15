package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.ChatColor;


public class Commandkick extends EssentialsCommand
{
	public Commandkick()
	{
		super("kick");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User u;
		try
		{
			u = ess.getUser(server.matchPlayer(args[0]).get(0));
		}
		catch (Throwable ex)
		{
			sender.sendMessage(Util.i18n("playerNotFound"));
			return;
		}

		charge(sender);
		final String kickReason = args.length > 1 ? getFinalArg(args, 1) : Util.i18n("kickDefault");
		u.kickPlayer(kickReason);
		server.broadcastMessage(Util.format("playerKicked", u.getName(), kickReason));
	}
}
