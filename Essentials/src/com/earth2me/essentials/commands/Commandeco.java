package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;


public class Commandeco extends EssentialsCommand
{
	public Commandeco()
	{
		super("eco");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		EcoCommands cmd;
		int amount;
		try
		{
			cmd = EcoCommands.valueOf(args[0].toUpperCase());
			amount = Integer.parseInt(args[2].replaceAll("[^0-9]", ""));
		}
		catch (Exception ex)
		{
			sender.sendMessage("§cUsage: /eco [give|take|reset] [player] [money]");
			return;
		}

		if (args[1].contentEquals("*"))
		{
			for (Player p : server.getOnlinePlayers())
			{
				User u = User.get(p);
				switch (cmd)
				{
				case GIVE:
					u.giveMoney(amount);
					break;

				case TAKE:
					u.takeMoney(amount);
					break;

				case RESET:
					u.setMoney(amount == 0 ? Essentials.getSettings().getStartingBalance() : amount);
					break;
				}
			}
		}
		else
		{
			for (Player p : server.matchPlayer(args[1]))
			{
				User u = User.get(p);
				switch (cmd)
				{
				case GIVE:
					u.giveMoney(amount);
					break;

				case TAKE:
					u.takeMoney(amount);
					break;

				case RESET:
					u.setMoney(amount == 0 ? Essentials.getSettings().getStartingBalance() : amount);
					break;
				}
			}
		}
	}


	private enum EcoCommands
	{
		GIVE, TAKE, RESET
	}
}
