package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandenderchest extends EssentialsCommand
{
	public Commandenderchest()
	{
		super("enderchest");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0 && user.isAuthorized("essentials.enderchest.others"))
		{
			final User invUser = getPlayer(server, user, args, 0);
			user.getBase().closeInventory();
			user.getBase().openInventory(invUser.getBase().getEnderChest());
			user.setEnderSee(true);
		}
		else
		{
			user.getBase().closeInventory();
			user.getBase().openInventory(user.getBase().getEnderChest());
			user.setEnderSee(false);
		}

	}
}
