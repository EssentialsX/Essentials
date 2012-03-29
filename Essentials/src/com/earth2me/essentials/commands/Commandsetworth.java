package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;


public class Commandsetworth extends EssentialsCommand
{
	public Commandsetworth()
	{
		super("setworth");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		ItemStack stack;
		String price;

		if (args.length == 1)
		{
			stack = user.getInventory().getItemInHand();
			price = args[0];
		}
		else
		{
			stack = ess.getItemDb().get(args[0]);
			price = args[1];
		}

		ess.getWorth().setPrice(stack, Double.parseDouble(price));
		user.sendMessage(_("worthSet"));
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		ItemStack stack = ess.getItemDb().get(args[0]);
		ess.getWorth().setPrice(stack, Double.parseDouble(args[1]));
		sender.sendMessage(_("worthSet"));
	}
}
