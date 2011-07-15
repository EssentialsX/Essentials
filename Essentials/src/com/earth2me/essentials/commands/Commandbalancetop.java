package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
		HashMap<User, Double> balances = new HashMap<User, Double>();
		for (Map.Entry<String, User> u : ess.getAllUsers().entrySet())
		{
			balances.put(u.getValue(), u.getValue().getMoney());
		}
		int count = 0;
		server.broadcastMessage(Util.format("balanceTop", max));
		for (Iterator i = Util.sortMapByValueDesc(balances).iterator(); i.hasNext();)
		{
			if (count == max)
			{
				break;
			}
			User key = (User)i.next();
			
			server.broadcastMessage(key.getDisplayName() + ", " + Util.formatCurrency(balances.get(key)));
			if(!(sender instanceof Player))
			{
			sender.sendMessage(key.getDisplayName() + ", " + Util.formatCurrency(balances.get(key)));
			}
			count++;
		}
	}
}
