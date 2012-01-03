package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.user.Inventory;
import java.util.Arrays;
import org.bukkit.inventory.ItemStack;


public class Commandinvsee extends EssentialsCommand
{
	@Override
	protected void run(final IUser user, final String[] args) throws Exception
	{

		if (args.length < 1 && user.getData().getInventory() == null)
		{
			throw new NotEnoughArgumentsException();
		}
		IUser invUser = user;
		if (args.length == 1)
		{
			invUser = getPlayer(args, 0);
		}
		user.acquireWriteLock();
		if (invUser == user && user.getData().getInventory() != null)
		{
			invUser.getInventory().setContents(user.getData().getInventory().getBukkitInventory());
			user.getData().setInventory(null);
			user.sendMessage(_("invRestored"));
			throw new NoChargeException();
		}
		if (user.getData().getInventory() == null)
		{
			user.getData().setInventory(new Inventory(user.getInventory().getContents()));
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
