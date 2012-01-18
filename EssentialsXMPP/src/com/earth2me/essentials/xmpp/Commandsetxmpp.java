package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;


public class Commandsetxmpp extends EssentialsCommand
{
	@Override
	protected void run(final IUser user, final String commandLabel, final String[] args) throws NotEnoughArgumentsException
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		EssentialsXMPP.getInstance().setAddress(user, args[0]);
		user.sendMessage("XMPP address set to " + args[0]);
	}
}
