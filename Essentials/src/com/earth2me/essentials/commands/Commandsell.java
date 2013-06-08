package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import com.earth2me.essentials.utils.NumberUtil;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;


public class Commandsell extends EssentialsCommand
{
	public Commandsell()
	{
		super("sell");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		BigDecimal totalWorth = BigDecimal.ZERO;
		String type = "";
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		ItemStack is = null;
		if (args[0].equalsIgnoreCase("hand"))
		{
			is = user.getItemInHand();
		}
		else if (args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("invent") || args[0].equalsIgnoreCase("all"))
		{
			for (ItemStack stack : user.getInventory().getContents())
			{
				if (stack == null || stack.getType() == Material.AIR)
				{
					continue;
				}
				try
				{
					totalWorth = totalWorth.add(sellItem(user, stack, args, true));
				}
				catch (Exception e)
				{
				}
			}
			if (totalWorth.signum() > 0)
			{
				user.sendMessage(_("totalWorthAll", type, NumberUtil.displayCurrency(totalWorth, ess)));
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("blocks"))
		{
			for (ItemStack stack : user.getInventory().getContents())
			{
				if (stack == null || stack.getTypeId() > 255 || stack.getType() == Material.AIR)
				{
					continue;
				}
				try
				{
					totalWorth = totalWorth.add(sellItem(user, stack, args, true));
				}
				catch (Exception e)
				{
				}
			}
			if (totalWorth.signum() > 0)
			{
				user.sendMessage(_("totalWorthBlocks", type, NumberUtil.displayCurrency(totalWorth, ess)));
			}
			return;
		}
		if (is == null)
		{
			is = ess.getItemDb().get(args[0]);
		}
		sellItem(user, is, args, false);
	}

	private BigDecimal sellItem(User user, ItemStack is, String[] args, boolean isBulkSell) throws Exception
	{
		if (is == null || is.getType() == Material.AIR)
		{
			throw new Exception(_("itemSellAir"));
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
		BigDecimal worth = ess.getWorth().getPrice(is);
		boolean stack = args.length > 1 && args[1].endsWith("s");
		boolean requireStack = ess.getSettings().isTradeInStacks(id);

		if (worth == null)
		{
			throw new Exception(_("itemCannotBeSold"));
		}
		if (requireStack && !stack)
		{
			throw new Exception(_("itemMustBeStacked"));
		}


		int max = 0;
		for (ItemStack s : user.getInventory().getContents())
		{
			if (s == null || !s.isSimilar(is))
			{
				continue;
			}
			max += s.getAmount();
		}

		if (stack)
		{
			amount *= is.getType().getMaxStackSize();
		}
		if (amount < 1)
		{
			amount += max;
		}

		if (requireStack)
		{
			amount -= amount % is.getType().getMaxStackSize();
		}
		if (amount > max || amount < 1)
		{
			if (!isBulkSell)
			{
				user.sendMessage(_("itemNotEnough1"));
				user.sendMessage(_("itemNotEnough2"));
				throw new Exception(_("itemNotEnough3"));
			}
			else
			{
				return worth.multiply(BigDecimal.valueOf(amount));
			}
		}

		BigDecimal result = worth.multiply(BigDecimal.valueOf(amount));
		//TODO: Prices for Enchantments
		final ItemStack ris = is.clone();
		ris.setAmount(amount);
		if (!user.getInventory().containsAtLeast(ris, amount)) {
			// This should never happen.
			throw new IllegalStateException("Trying to remove more items than are available.");
		}
		user.getInventory().removeItem(ris);
		user.updateInventory();
		Trade.log("Command", "Sell", "Item", user.getName(), new Trade(ris, ess), user.getName(), new Trade(result, ess), user.getLocation(), ess);
		user.giveMoney(result);
		user.sendMessage(_("itemSold", NumberUtil.displayCurrency(result, ess), amount, is.getType().toString().toLowerCase(Locale.ENGLISH), NumberUtil.displayCurrency(worth, ess)));
		logger.log(Level.INFO, _("itemSoldConsole", user.getDisplayName(), is.getType().toString().toLowerCase(Locale.ENGLISH), NumberUtil.displayCurrency(result, ess), amount, NumberUtil.displayCurrency(worth, ess)));
		return result;
	}
}
