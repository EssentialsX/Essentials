package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.ItemDb;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.inventory.ItemStack;


public class Commandworth extends EssentialsCommand
{
	public Commandworth()
	{
		super("worth");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		ItemStack is = user.getInventory().getItemInHand();
		int id = is.getTypeId();
		int amount = is.getAmount();

		try
		{
			if (args.length > 0) id = Integer.parseInt(args[0]);
		}
		catch (NumberFormatException ex)
		{
			id = ItemDb.get(args[0]).getTypeId();
		}

		try
		{
		if (args.length > 1) amount = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException ex)
		{
			amount = 64;
		}

		int worth = Essentials.getWorth().config.getInt("worth-" + id, 0);

		user.charge(this);
		user.sendMessage("§7Stack of " + id + " worth §c$" + (worth * amount) + "§7 (" + amount + " item(s) at $" + worth + " each)");
	}
}
