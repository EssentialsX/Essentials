package com.earth2me.essentials.commands;

import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import java.util.Locale;
import lombok.Cleanup;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


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
			cmd = EcoCommands.valueOf(args[0].toUpperCase(Locale.ENGLISH));
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
				final IUser player = ess.getUser(onlinePlayer);
				switch (cmd)
				{
				case GIVE:
					player.giveMoney(amount);
					break;

				case TAKE:
					player.takeMoney(amount);
					break;

				case RESET:
					@Cleanup ISettings settings = ess.getSettings();
					settings.acquireReadLock();
					player.setMoney(amount == 0 ? settings.getData().getEconomy().getStartingBalance() : amount);
					break;
				}
			}
		}
		else
		{
			final IUser player = getPlayer(server, args, 1, true);
			switch (cmd)
			{
			case GIVE:
				player.giveMoney(amount, sender);
				break;

			case TAKE:
				player.takeMoney(amount, sender);
				break;

			case RESET:
				@Cleanup ISettings settings = ess.getSettings();
				settings.acquireReadLock();
				player.setMoney(amount == 0 ? settings.getData().getEconomy().getStartingBalance() : amount);
				break;
			}
		}
	}


	private enum EcoCommands
	{
		GIVE, TAKE, RESET
	}
}
