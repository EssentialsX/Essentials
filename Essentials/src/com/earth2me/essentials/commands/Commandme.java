package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandme extends EssentialsCommand
{
	public Commandme()
	{
		super("me");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (user.isMuted())
		{
			user.sendMessage(Util.i18n("voiceSilenced"));
			return;
		}

		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		StringBuilder message = new StringBuilder();
		for (int i = 0; i < args.length; i++)
		{
			message.append(args[i]);
			message.append(' ');
		}
		charge(user);
		ess.broadcastMessage(user.getName(), "* " + user.getDisplayName() + " " + message);
	}
}
