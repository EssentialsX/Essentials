package com.earth2me.essentials.commands;

import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import java.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Commandgive extends EssentialsCommand
{
	public Commandgive()
	{
		super("give");
	}

	//TODO: move these messages to message file
	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		final ItemStack stack = ess.getItemDb().get(args[1]);

		final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
		if (sender instanceof Player
			&& (!ess.getUser((Player)sender).isAuthorized("essentials.give.item-" + itemname)
				&& !ess.getUser((Player)sender).isAuthorized("essentials.give.item-" + stack.getTypeId())))
		{
			throw new Exception(ChatColor.RED + "You are not allowed to spawn the item " + itemname);
		}

		final IUser giveTo = getPlayer(server, args, 0);

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
		if (args.length > 2 && Integer.parseInt(args[2]) > 0)
		{
			stack.setAmount(Integer.parseInt(args[2]));
		}
		else if (defaultStackSize > 0)
		{
			stack.setAmount(defaultStackSize);
		}
		else if (oversizedStackSize > 0 && giveTo.isAuthorized("essentials.oversizedstacks"))
		{
			stack.setAmount(oversizedStackSize);
		}

		if (args.length > 3)
		{
			for (int i = 3; i < args.length; i++)
			{
				final String[] split = args[i].split("[:+',;.]", 2);
				if (split.length < 1)
				{
					continue;
				}
				final Enchantment enchantment = Commandenchant.getEnchantment(split[0], sender instanceof Player ? ess.getUser((Player)sender) : null);
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
			throw new Exception(ChatColor.RED + "You can't give air.");
		}

		final String itemName = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ');
		sender.sendMessage(ChatColor.BLUE + "Giving " + stack.getAmount() + " of " + itemName + " to " + giveTo.getDisplayName() + ".");
		if (giveTo.isAuthorized("essentials.oversizedstacks"))
		{
			InventoryWorkaround.addItem(giveTo.getInventory(), true, oversizedStackSize, stack);
		}
		else
		{
			InventoryWorkaround.addItem(giveTo.getInventory(), true, stack);
		}
		giveTo.updateInventory();
	}
}
