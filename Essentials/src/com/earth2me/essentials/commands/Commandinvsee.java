package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandinvsee extends EssentialsCommand
{
	public Commandinvsee()
	{
		super("invsee");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final User invUser = getPlayer(server, args, 0);
		user.setInvSee(true);
		user.openInventory(invUser.getInventory());
	}
}
