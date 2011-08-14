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
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
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
			String list = getList(target);
			user.sendMessage(list);
		}
		else if (args[0].equalsIgnoreCase("clear"))
		{
			List<Integer> itemList = target.getUnlimited();

			int index = 0;
			while (itemList.size() > index)
			{
				Integer item = itemList.get(index);
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

	private String getList(User target)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Util.i18n("unlimitedItems")).append(" ");
		boolean first = true;
		List<Integer> items = target.getUnlimited();
		if (items.isEmpty())
		{
			sb.append(Util.i18n("none"));
		}
		for (Integer integer : items)
		{
			if (!first)
			{
				sb.append(", ");
			}
			first = false;
			String matname = Material.getMaterial(integer).toString().toLowerCase().replace("_", "");
			sb.append(matname);
		}

		return sb.toString();
	}

	private Boolean toggleUnlimited(User user, User target, String item) throws Exception
	{
		ItemStack stack = ess.getItemDb().get(item, 1);
		stack.setAmount(Math.min(stack.getType().getMaxStackSize(), 2));

		String itemname = stack.getType().toString().toLowerCase().replace("_", "");
		if (ess.getSettings().permissionBasedItemSpawn()
			&& (!user.isAuthorized("essentials.unlimited.item-all")
				&& !user.isAuthorized("essentials.unlimited.item-" + itemname)
				&& !user.isAuthorized("essentials.unlimited.item-" + stack.getTypeId())
				&& !((stack.getType() == Material.WATER_BUCKET || stack.getType() == Material.LAVA_BUCKET)
					 && user.isAuthorized("essentials.unlimited.item-bucket"))))
		{
			user.sendMessage(Util.format("unlimitedItemPermission", itemname));
			return false;
		}

		String message = "disableUnlimited";
		Boolean enableUnlimited = false;
		if (!target.hasUnlimited(stack))
		{
			message = "enableUnlimited";
			enableUnlimited = true;
			charge(user);
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
