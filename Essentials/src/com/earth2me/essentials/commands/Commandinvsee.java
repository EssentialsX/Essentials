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
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{

		if (args.length < 1 && user.getSavedInventory() == null)
		{
			throw new NotEnoughArgumentsException();
		}
		User invUser = user;
		if (args.length == 1)
		{
			invUser = getPlayer(server, args, 0);
		}
		if (invUser == user && user.getSavedInventory() != null)
		{
			invUser.getInventory().setContents(user.getSavedInventory());
			user.setSavedInventory(null);
			user.sendMessage("Your inventory has been restored.");
			return;
		}

		charge(user);
		if (user.getSavedInventory() == null)
		{
			user.setSavedInventory(user.getInventory().getContents());
		}
		user.getInventory().setContents(invUser.getInventory().getContents());
		user.sendMessage("You see the inventory of " + invUser.getDisplayName() + ".");
		user.sendMessage("Use /invsee to restore your inventory.");
	}
}
