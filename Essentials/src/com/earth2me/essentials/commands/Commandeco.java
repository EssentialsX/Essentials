package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;


public class Commandeco extends EssentialsCommand
{
	public Commandeco()
	{
		super("eco");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		EcoCommands cmd;
		double amount;
		try
		{
			cmd = EcoCommands.valueOf(args[0].toUpperCase());
			amount = Double.parseDouble(args[2].replaceAll("[^0-9\\.]", ""));
		}
		catch (Exception ex)
		{
			throw new NotEnoughArgumentsException(ex);
		}

		if (args[1].contentEquals("*"))
		{
			for (Player onlinePlayer : server.getOnlinePlayers())
			{
				final User player = ess.getUser(onlinePlayer);
				switch (cmd)
				{
				case GIVE:
					player.giveMoney(amount);
					break;

				case TAKE:
					player.takeMoney(amount);
					break;

				case RESET:
					player.setMoney(amount == 0 ? ess.getSettings().getStartingBalance() : amount);
					break;
				}
			}
		}
		else
		{
			final User player = getPlayer(server, args, 1, true);
			switch (cmd)
			{
			case GIVE:
				player.giveMoney(amount, sender);
				break;

			case TAKE:
				player.takeMoney(amount, sender);
				break;

			case RESET:
				player.setMoney(amount == 0 ? ess.getSettings().getStartingBalance() : amount);
				break;
			}
		}
	}


	private enum EcoCommands
	{
		GIVE, TAKE, RESET
	}
}
