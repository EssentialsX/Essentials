package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;


public class Commanditem extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final ItemStack stack = ess.getItemDb().get(args[0]);

		final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
		if (!user.isAuthorized("essentials.itemspawn.item-" + itemname)
			&& !user.isAuthorized("essentials.itemspawn.item-" + stack.getTypeId()))
		{
			throw new Exception(_("cantSpawnItem", itemname));
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
		if (args.length > 1 && Integer.parseInt(args[1]) > 0)
		{
			stack.setAmount(Integer.parseInt(args[1]));
		}
		else if (defaultStackSize > 0)
		{
			stack.setAmount(defaultStackSize);
		}
		else if (oversizedStackSize > 0 && user.isAuthorized("essentials.oversizedstacks"))
		{
			stack.setAmount(oversizedStackSize);
		}

		if (args.length > 2)
		{
			for (int i = 2; i < args.length; i++)
			{
				final String[] split = args[i].split("[:+',;.]", 2);
				if (split.length < 1)
				{
					continue;
				}
				final Enchantment enchantment = Commandenchant.getEnchantment(split[0], user);
				int level;
				if (split.length > 1)
				{
					level = Integer.parseInt(split[1]);
				}
				else
				{
					level = enchantment.getMaxLevel();
				}
				stack.addEnchantment(enchantment, level);
			}
		}

		if (stack.getType() == Material.AIR)
		{
			throw new Exception(_("cantSpawnItem", "Air"));
		}

		final String displayName = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ');
		user.sendMessage(_("itemSpawn", stack.getAmount(), displayName));
		if (user.isAuthorized("essentials.oversizedstacks"))
		{
			InventoryWorkaround.addItem(user.getInventory(), true, oversizedStackSize, stack);
		}
		else
		{
			InventoryWorkaround.addItem(user.getInventory(), true, stack);
		}
		user.updateInventory();
	}
}
