package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.ItemPermissions;
import com.earth2me.essentials.perm.Permissions;
import java.util.Locale;
import org.bukkit.inventory.ItemStack;


public class Commandmore extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		final ItemStack stack = user.getItemInHand();
		if (stack == null)
		{
			throw new Exception(_("cantSpawnItem", "Air"));
		}
		int defaultStackSize = 0;
		int oversizedStackSize = 0;
		ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		try
		{
			defaultStackSize = settings.getData().getGeneral().getDefaultStacksize();
			oversizedStackSize = settings.getData().getGeneral().getOversizedStacksize();
		}
		finally
		{
			settings.unlock();
		}
		if (stack.getAmount() >= (Permissions.OVERSIZEDSTACKS.isAuthorized(user)
								  ? oversizedStackSize
								  : defaultStackSize > 0 ? defaultStackSize : stack.getMaxStackSize()))
		{
			throw new NoChargeException();
		}
		final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
		if (!ItemPermissions.getPermission(stack.getType()).isAuthorized(user))
		{
			throw new Exception(_("cantSpawnItem", itemname));
		}
		if (Permissions.OVERSIZEDSTACKS.isAuthorized(user))
		{
			stack.setAmount(oversizedStackSize);
		}
		else
		{
			stack.setAmount(defaultStackSize > 0 ? defaultStackSize : stack.getMaxStackSize());
		}
		user.updateInventory();
	}
}