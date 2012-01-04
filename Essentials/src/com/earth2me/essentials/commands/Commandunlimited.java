package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.Cleanup;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class Commandunlimited extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		@Cleanup
		IUser target = user;

		if (args.length > 1 && user.isAuthorized("essentials.unlimited.others"))
		{
			target = getPlayer(args, 1);
			target.acquireReadLock();
		}

		if (args[0].equalsIgnoreCase("list"))
		{
			final String list = getList(target);
			user.sendMessage(list);
		}
		else if (args[0].equalsIgnoreCase("clear"))
		{
			//TODO: Fix this, the clear should always work, even when the player does not have permission.
			final Set<Material> itemList = target.getData().getUnlimited();
			for(Material mat : itemList)
			{
				toggleUnlimited(user, target, mat.name());
				
			}
		}
		else
		{
			toggleUnlimited(user, target, args[0]);
		}
	}

	private String getList(final IUser target)
	{
		final StringBuilder output = new StringBuilder();
		output.append(_("unlimitedItems")).append(" ");
		boolean first = true;
		final Set<Material> items = target.getData().getUnlimited();
		if (items.isEmpty())
		{
			output.append(_("none"));
		}
		for (Material mater : items)
		{
			if (!first)
			{
				output.append(", ");
			}
			first = false;
			final String matname = mater.name().toLowerCase(Locale.ENGLISH).replace("_", "");
			output.append(matname);
		}

		return output.toString();
	}

	private Boolean toggleUnlimited(final IUser user, final IUser target, final String item) throws Exception
	{
		final ItemStack stack = ess.getItemDb().get(item, 1);
		stack.setAmount(Math.min(stack.getType().getMaxStackSize(), 2));

		final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
		if (!user.isAuthorized("essentials.unlimited.item-" + itemname)
			&& !user.isAuthorized("essentials.unlimited.item-" + stack.getTypeId()))
		{
			throw new Exception(_("unlimitedItemPermission", itemname));
		}

		String message = "disableUnlimited";
		Boolean enableUnlimited = false;
		if (!target.getData().hasUnlimited(stack.getType()))
		{
			message = "enableUnlimited";
			enableUnlimited = true;
			if (!InventoryWorkaround.containsItem(target.getInventory(), true, true, stack))
			{
				target.getInventory().addItem(stack);
			}
		}

		if (user != target)
		{
			user.sendMessage(_(message, itemname, target.getDisplayName()));
		}
		target.sendMessage(_(message, itemname, target.getDisplayName()));
		target.getData().setUnlimited(stack.getType(), enableUnlimited);

		return true;
	}
}
