package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


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
		Map<Double, User> balances = new TreeMap<Double, User>(Collections.reverseOrder());
		for (Map.Entry<String, User> u : ess.getAllUsers().entrySet())
		{
			balances.put(u.getValue().getMoney(), u.getValue());
		}
		int count = 0;
		sender.sendMessage(Util.format("balanceTop", max));
		for (Map.Entry<Double, User> ba : balances.entrySet())
		{
			if (count == max)
			{
				break;
			}
			sender.sendMessage(ba.getValue().getDisplayName() + ", " + Util.formatCurrency(ba.getKey()));
			count++;
		}
	}
}
