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
			user.sendMessage(sb.toString());
			return;
		}

		ItemStack stack = ess.getItemDb().get(args[0], 1);

		String itemname = stack.getType().toString().toLowerCase().replace("_", "");
		if (!user.isAuthorized("essentials.unlimited.item-all")
			&& !user.isAuthorized("essentials.unlimited.item-" + itemname)
			&& !user.isAuthorized("essentials.unlimited.item-" + stack.getTypeId())
			&& !((stack.getType() == Material.WATER_BUCKET || stack.getType() == Material.LAVA_BUCKET)
				 && user.isAuthorized("essentials.unlimited.item-bucket")))
		{
			user.sendMessage(Util.format("unlimitedItemPermission", itemname));
			return;
		}


		if (target.hasUnlimited(stack))
		{
			if (user != target)
			{
				user.sendMessage(Util.format("disableUnlimited", itemname, target.getDisplayName()));
			}
			target.sendMessage(Util.format("disableUnlimited", itemname, target.getDisplayName()));
			target.setUnlimited(stack, false);
			return;
		}
		charge(user);
		if (user != target)
		{
			user.sendMessage(Util.format("enableUnlimited", itemname, target.getDisplayName()));
		}
		target.sendMessage(Util.format("enableUnlimited", itemname, target.getDisplayName()));
		if (!InventoryWorkaround.containsItem(target.getInventory(), true, stack))
		{
			target.getInventory().addItem(stack);
		}
		target.setUnlimited(stack, true);
	}
}
