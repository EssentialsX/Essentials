package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
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
			user.sendMessage(_("invRestored"));
			throw new NoChargeException();
		}

		if (user.getSavedInventory() == null)
		{
			user.setSavedInventory(user.getInventory().getContents());
		}
		ItemStack[] invUserStack = invUser.getInventory().getContents();
		final int userStackLength = user.getInventory().getContents().length;
		if (invUserStack.length < userStackLength)
		{
			invUserStack = Arrays.copyOf(invUserStack, userStackLength);
		}
		if (invUserStack.length > userStackLength)
		{
			throw new Exception(_("invBigger"));
		}
		user.getInventory().setContents(invUserStack);
		user.sendMessage(_("invSee", invUser.getDisplayName()));
		user.sendMessage(_("invSeeHelp"));
		throw new NoChargeException();
	}
}
