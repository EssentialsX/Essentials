package com.earth2me.essentials.commands;


import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;


public class Commandworth extends EssentialsCommand
{
	public Commandworth()
	{
		super("worth");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		ItemStack is = user.getInventory().getItemInHand();
		int amount = is.getAmount();

		if (args.length > 0)
		{
			is = ess.getItemDb().get(args[0]);
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
			amount = is.getType().getMaxStackSize();
		}

		is.setAmount(amount);
		double worth = ess.getWorth().getPrice(is);
		if (Double.isNaN(worth))
		{
			throw new Exception(Util.i18n("itemCannotBeSold"));
		}

		user.sendMessage(is.getDurability() != 0
						 ? Util.format("worthMeta",
									   is.getType().toString().toLowerCase().replace("_", ""),
									   is.getDurability(),
									   Util.formatCurrency(worth * amount, ess),
									   amount,
									   Util.formatCurrency(worth, ess))
						 : Util.format("worth",
									   is.getType().toString().toLowerCase().replace("_", ""),
									   Util.formatCurrency(worth * amount, ess),
									   amount,
									   Util.formatCurrency(worth, ess)));
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		ItemStack is = ess.getItemDb().get(args[0]);
		int amount = is.getAmount();

		try
		{
			if (args.length > 1)
			{
				amount = Integer.parseInt(args[1]);
			}
		}
		catch (NumberFormatException ex)
		{
			amount = is.getType().getMaxStackSize();
		}

		is.setAmount(amount);
		double worth = ess.getWorth().getPrice(is);
		if (Double.isNaN(worth))
		{
			throw new Exception(Util.i18n("itemCannotBeSold"));
		}

		sender.sendMessage(is.getDurability() != 0
						   ? Util.format("worthMeta",
										 is.getType().toString().toLowerCase().replace("_", ""),
										 is.getDurability(),
										 Util.formatCurrency(worth * amount, ess),
										 amount,
										 Util.formatCurrency(worth, ess))
						   : Util.format("worth",
										 is.getType().toString().toLowerCase().replace("_", ""),
										 Util.formatCurrency(worth * amount, ess),
										 amount,
										 Util.formatCurrency(worth, ess)));

	}
}
