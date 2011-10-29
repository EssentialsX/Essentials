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
			throw new Exception(Util.i18n("voiceSilenced"));
		}

		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		String message = getFinalArg(args, 1);
		if (user.isAuthorized("essentials.chat.color"))
		{
			message = message.replaceAll("&([0-9a-f])", "§$1");
		}

		ess.broadcastMessage(user, Util.format("action", user.getDisplayName(), message));
	}
}
