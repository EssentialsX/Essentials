package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
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
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
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
			user.sendMessage(Util.i18n("invRestored"));
			throw new NoChargeException();
		}

		if (user.getSavedInventory() == null)
		{
			user.setSavedInventory(user.getInventory().getContents());
		}
		ItemStack[] invUserStack = invUser.getInventory().getContents();
		final int userStackLength = user.getInventory().getContents().length;
		if (invUserStack.length < userStackLength) {
			invUserStack = Arrays.copyOf(invUserStack, userStackLength);
		}
		if (invUserStack.length > userStackLength) {
			throw new Exception(Util.i18n("invBigger"));
		}
		user.getInventory().setContents(invUserStack);
		user.sendMessage(Util.format("invSee", invUser.getDisplayName()));
		user.sendMessage(Util.i18n("invSeeHelp"));
		throw new NoChargeException();
	}
}
