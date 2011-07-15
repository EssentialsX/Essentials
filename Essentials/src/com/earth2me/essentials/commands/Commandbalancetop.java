package com.earth2me.essentials.commands;

import java.util.Map.Entry;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;


public class Commandbalancetop extends EssentialsCommand
{
	public Commandbalancetop()
	{
		super("balancetop");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		int max = 10;
		if (args.length > 0)
		{
			try
			{
				if (Integer.parseInt(args[0]) < 10)
				{
					max = Integer.parseInt(args[0]);
				}
			}
			catch (NumberFormatException ex)
			{
				//catch it because they tried to enter a string not number.
			}
		}
		final Map<User, Double> balances = new HashMap<User, Double>();
		for (User u : ess.getAllUsers().values())
		{
			balances.put(u, u.getMoney());
		}

		final List<Map.Entry<User, Double>> sortedEntries = new ArrayList<Map.Entry<User, Double>>(balances.entrySet());
		Collections.sort(sortedEntries, new Comparator<Map.Entry<User, Double>>()
		{
			public int compare(final Entry<User, Double> entry1, final Entry<User, Double> entry2)
			{
				return -entry1.getValue().compareTo(entry2.getValue());
			}
		});
		int count = 0;
		sender.sendMessage(Util.format("balanceTop", max));
		for (Map.Entry<User, Double> entry : sortedEntries)
		{
			if (count == max)
			{
				break;
			}
			sender.sendMessage(entry.getKey().getDisplayName() + ", " + Util.formatCurrency(entry.getValue()));
			count++;
		}
	}
}
