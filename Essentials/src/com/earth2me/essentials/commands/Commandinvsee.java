package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import java.util.Arrays;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;


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
		ItemStack[] invUserStack = invUser.getInventory().getContents();
		int userStackLength = user.getInventory().getContents().length;
		if (invUserStack.length < userStackLength) {
			invUserStack = Arrays.copyOf(invUserStack, userStackLength);
		}
		if (invUserStack.length > userStackLength) {
			throw new Exception("The other users inventory is bigger than yours.");
		}
		user.getInventory().setContents(invUserStack);
		user.sendMessage("You see the inventory of " + invUser.getDisplayName() + ".");
		user.sendMessage("Use /invsee to restore your inventory.");
	}
}
