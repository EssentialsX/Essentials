package com.earth2me.essentials.commands;

import com.earth2me.essentials.InventoryWorkaround;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;


public class Commandunlimited extends EssentialsCommand
{
	public Commandunlimited()
	{
		super("unlimited");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User target = user;

		if (args.length > 1 && user.isAuthorized("essentials.unlimited.others"))
		{
			target = getPlayer(server, args, 1);
		}

		if (args[0].equalsIgnoreCase("list"))
		{
			final String list = getList(target);
			user.sendMessage(list);
		}
		else if (args[0].equalsIgnoreCase("clear"))
		{
			final List<Integer> itemList = target.getUnlimited();

			int index = 0;
			while (itemList.size() > index)
			{
				final Integer item = itemList.get(index);
				if (toggleUnlimited(user, target, item.toString()) == false)
				{
					index++;
				}
			}
		}
		else
		{
			toggleUnlimited(user, target, args[0]);
		}
	}

	private String getList(final User target)
	{
		final StringBuilder output = new StringBuilder();
		output.append(Util.i18n("unlimitedItems")).append(" ");
		boolean first = true;
		final List<Integer> items = target.getUnlimited();
		if (items.isEmpty())
		{
			output.append(Util.i18n("none"));
		}
		for (Integer integer : items)
		{
			if (!first)
			{
				output.append(", ");
			}
			first = false;
			final String matname = Material.getMaterial(integer).toString().toLowerCase().replace("_", "");
			output.append(matname);
		}

		return output.toString();
	}

	private Boolean toggleUnlimited(final User user, final User target, final String item) throws Exception
	{
		final ItemStack stack = ess.getItemDb().get(item, 1);
		stack.setAmount(Math.min(stack.getType().getMaxStackSize(), 2));

		final String itemname = stack.getType().toString().toLowerCase().replace("_", "");
		if (ess.getSettings().permissionBasedItemSpawn()
			&& (!user.isAuthorized("essentials.unlimited.item-all")
				&& !user.isAuthorized("essentials.unlimited.item-" + itemname)
				&& !user.isAuthorized("essentials.unlimited.item-" + stack.getTypeId())
				&& !((stack.getType() == Material.WATER_BUCKET || stack.getType() == Material.LAVA_BUCKET)
					 && user.isAuthorized("essentials.unlimited.item-bucket"))))
		{
			throw new Exception(Util.format("unlimitedItemPermission", itemname));
		}

		String message = "disableUnlimited";
		Boolean enableUnlimited = false;
		if (!target.hasUnlimited(stack))
		{
			message = "enableUnlimited";
			enableUnlimited = true;
			if (!InventoryWorkaround.containsItem(target.getInventory(), true, stack))
			{
				target.getInventory().addItem(stack);
			}
		}

		if (user != target)
		{
			user.sendMessage(Util.format(message, itemname, target.getDisplayName()));
		}
		target.sendMessage(Util.format(message, itemname, target.getDisplayName()));
		target.setUnlimited(stack, enableUnlimited);

		return true;
	}
}
