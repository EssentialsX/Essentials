package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.Locale;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;


public class Commandworth extends EssentialsCommand
{
	public Commandworth()
	{
		super("worth");
	}

	//TODO: Remove duplication
	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		ItemStack iStack = user.getInventory().getItemInHand();
		int amount = iStack.getAmount();

		if (args.length > 0)
		{
			iStack = ess.getItemDb().get(args[0]);
		}

		try
		{
			if (args.length > 1)
			{
				amount = Integer.parseInt(args[1]);
			}
		}
		catch (NumberFormatException ex)
		{
			amount = iStack.getType().getMaxStackSize();
		}

		iStack.setAmount(amount);
		final double worth = ess.getWorth().getPrice(iStack);
		if (Double.isNaN(worth))
		{
			throw new Exception(_("itemCannotBeSold"));
		}

		user.sendMessage(iStack.getDurability() != 0
						 ? _("worthMeta",
							 iStack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", ""),
							 iStack.getDurability(),
							 Util.displayCurrency(worth * amount, ess),
							 amount,
							 Util.displayCurrency(worth, ess))
						 : _("worth",
							 iStack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", ""),
							 Util.displayCurrency(worth * amount, ess),
							 amount,
							 Util.displayCurrency(worth, ess)));
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		ItemStack iStack = ess.getItemDb().get(args[0]);
		int amount = iStack.getAmount();

		try
		{
			if (args.length > 1)
			{
				amount = Integer.parseInt(args[1]);
			}
		}
		catch (NumberFormatException ex)
		{
			amount = iStack.getType().getMaxStackSize();
		}

		iStack.setAmount(amount);
		final double worth = ess.getWorth().getPrice(iStack);
		if (Double.isNaN(worth))
		{
			throw new Exception(_("itemCannotBeSold"));
		}

		sender.sendMessage(iStack.getDurability() != 0
						   ? _("worthMeta",
							   iStack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", ""),
							   iStack.getDurability(),
							   Util.displayCurrency(worth * amount, ess),
							   amount,
							   Util.displayCurrency(worth, ess))
						   : _("worth",
							   iStack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", ""),
							   Util.displayCurrency(worth * amount, ess),
							   amount,
							   Util.displayCurrency(worth, ess)));

	}
}
