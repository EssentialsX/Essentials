package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandbroadcast extends EssentialsCommand
{
	public Commandbroadcast()
	{
		super("broadcast");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("Usage: /" + commandLabel + " [msg]");
		}

		StringBuilder message = new StringBuilder();
		for (int i = 0; i < args.length; i++)
		{
			message.append(args[i]);
			message.append(' ');
		}

		server.broadcastMessage("[§cBroadcast§f]§a " + message.toString());
	}
}
