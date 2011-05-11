package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.InventoryWorkaround;
import com.earth2me.essentials.ItemDb;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class Commandsell extends EssentialsCommand
{
	public Commandsell()
	{
		super("sell");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		ItemStack is = null;
		if (args[0].equalsIgnoreCase("hand"))
		{
			is = user.getItemInHand();
		}
		if (args[0].equalsIgnoreCase("inventory"))
		{
			for (ItemStack stack : user.getInventory().getContents())
			{
				if (stack == null || stack.getType() == Material.AIR) continue;
				sellItem(user, stack, args, true);
			}
			return;
		}
		if (args[0].equalsIgnoreCase("blocks"))
		{
			for (ItemStack stack : user.getInventory().getContents())
			{
				if (stack == null || stack.getTypeId() > 255 || stack.getType() == Material.AIR) continue;
				sellItem(user, stack, args, true);
			}
			return;
		}
		if (is == null)
		{
			is = ItemDb.get(args[0]);
		}
		sellItem(user, is, args, false);
	}

	private void sellItem(User user, ItemStack is, String[] args, boolean isBulkSell) throws Exception
	{
		if (is == null || is.getType() == Material.AIR)
		{
			throw new Exception(Util.i18n("itemSellAir"));
		}
		int id = is.getTypeId();
		int amount = 0;
		if (args.length > 1)
		{
			amount = Integer.parseInt(args[1].replaceAll("[^0-9]", ""));
			if (args[1].startsWith("-"))
			{
				amount = -amount;
			}
		}
		double worth = Essentials.getWorth().getPrice(is);
		boolean stack = args.length > 1 && args[1].endsWith("s");
		boolean requireStack = ess.getConfiguration().getBoolean("trade-in-stacks-" + id, false);

		if (Double.isNaN(worth))
		{
			throw new Exception(Util.i18n("itemCannotBeSold"));
		}
		if (requireStack && !stack)
		{
			throw new Exception(Util.i18n("itemMustBeStacked"));
		}


		int max = 0;
		if (!isBulkSell)
		{
			for (ItemStack s : user.getInventory().getContents())
			{
				if (s == null)
				{
					continue;
				}
				if (s.getTypeId() != is.getTypeId())
				{
					continue;
				}
				if (s.getDurability() != is.getDurability())
				{
					continue;
				}
				max += s.getAmount();
			}
		}
		else
		{
			max += is.getAmount();
		}

		if (stack)
		{
			amount *= 64;
		}
		if (amount < 1)
		{
			amount += max;
		}

		if (requireStack)
		{
			amount -= amount % 64;
		}

		if (amount > max || amount < 1)
		{
			user.sendMessage(Util.i18n("itemNotEnough1"));
			user.sendMessage(Util.i18n("itemNotEnough2"));
			user.sendMessage(Util.i18n("itemNotEnough3"));
			return;
		}

		charge(user);
		InventoryWorkaround.removeItem(user.getInventory(), true, new ItemStack(is.getType(), amount, is.getDurability()));
		user.updateInventory();
		user.giveMoney(worth * amount);
		user.sendMessage(Util.format("itemSold", Util.formatCurrency(worth * amount), amount,  Util.formatCurrency(worth)));
		logger.log(Level.INFO, Util.format("itemSoldConsole",user.getDisplayName(),is.getType().toString().toLowerCase(), Util.formatCurrency(worth * amount), amount, Util.formatCurrency(worth)));
	
	}
}
