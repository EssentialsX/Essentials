package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandme extends EssentialsCommand
{
	public Commandme()
	{
		super("me");
	}

	@Override
	public String[] getTriggers()
	{
		return new String[] { getName(), "describe", "action" };
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (user.isMuted())
		{ user.sendMessage("§7Your voice has been silenced");
		  return;
		}

		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /me [description]");
			return;
		}
		StringBuilder message = new StringBuilder();
		for (int i = 0; i < args.length; i++)
		{
			message.append(args[i]);
			message.append(' ');
		}
		user.charge(this);
		server.broadcastMessage("* " + user.getDisplayName() + " " + message);
	}
}
