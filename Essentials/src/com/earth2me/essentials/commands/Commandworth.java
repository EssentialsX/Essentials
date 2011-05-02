package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.ItemDb;
import com.earth2me.essentials.User;
import java.text.DecimalFormat;
import org.bukkit.inventory.ItemStack;


public class Commandworth extends EssentialsCommand
{
	private static DecimalFormat df = new DecimalFormat("0.##");
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
			is = ItemDb.get(args[0]);
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
			amount = 64;
		}

		is.setAmount(amount);
		double worth = Essentials.getWorth().getPrice(is);
		if (Double.isNaN(worth))
		{
			throw new Exception("That item cannot be sold to the server.");
		}

		user.charge(this);
		String d = df.format(Double.parseDouble(Double.toString(worth)));
		String d2 = df.format(Double.parseDouble(Double.toString(Double.parseDouble(d)*amount)));		
		user.sendMessage("§7Stack of " + is.getType().toString().toLowerCase().replace("_", "") + " worth §c$" + d2 + "§7 (" + amount + " item(s) at $" + d + " each)");
	}
}
