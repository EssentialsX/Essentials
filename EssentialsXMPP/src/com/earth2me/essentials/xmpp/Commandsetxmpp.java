package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Server;


public class Commandsetxmpp extends EssentialsCommand
{
	public Commandsetxmpp()
	{
		super("setxmpp");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		EssentialsXMPP.getInstance().setAddress(user, args[0]);
	}
}
