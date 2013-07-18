package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
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
		List<ItemStack> is = ess.getItemDb().getMatching(user, args);
		int count = 0;

		boolean isBulk = is.size() > 1;

		for (ItemStack stack : is)
		{
			try
			{
				if (stack.getAmount() > 0)
				{
					totalWorth = totalWorth.add(sellItem(user, stack, args, isBulk));
					stack = stack.clone();
					count++;
					for (ItemStack zeroStack : is)
					{
						if (zeroStack.isSimilar(stack))
						{
							zeroStack.setAmount(0);
						}
					}
				}
			}
			catch (Exception e)
			{
				if (!isBulk)
				{
					throw e;
				}
			}
		}
		if (count != 1)
		{
			if (args[0].equalsIgnoreCase("blocks"))
			{
				user.sendMessage(_("totalWorthBlocks", type, NumberUtil.displayCurrency(totalWorth, ess)));
			}
			else
			{
				user.sendMessage(_("totalWorthAll", type, NumberUtil.displayCurrency(totalWorth, ess)));
			}
		}
	}

	private BigDecimal sellItem(User user, ItemStack is, String[] args, boolean isBulkSell) throws Exception
	{
		int amount = ess.getWorth().getAmount(ess, user, is, args, isBulkSell);
		BigDecimal worth = ess.getWorth().getPrice(is);

		if (worth == null)
		{
			throw new Exception(_("itemCannotBeSold"));
		}

		if (amount <= 0)
		{
			if (!isBulkSell) {
				user.sendMessage(_("itemSold", NumberUtil.displayCurrency(BigDecimal.ZERO, ess), BigDecimal.ZERO, is.getType().toString().toLowerCase(Locale.ENGLISH), NumberUtil.displayCurrency(worth, ess)));
			}
			return BigDecimal.ZERO;
		}
		
		BigDecimal result = worth.multiply(BigDecimal.valueOf(amount));

		//TODO: Prices for Enchantments
		final ItemStack ris = is.clone();
		ris.setAmount(amount);
		if (!user.getInventory().containsAtLeast(ris, amount))
		{
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
