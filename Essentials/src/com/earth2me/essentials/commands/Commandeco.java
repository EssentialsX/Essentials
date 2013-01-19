package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.Locale;
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
		double startingBalance = (double)ess.getSettings().getStartingBalance();
		String start = ess.getSettings().getCurrencySymbol() + ess.getSettings().getStartingBalance();
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

		double min = ess.getSettings().getMinMoney();

		if (args[1].contentEquals("**"))
		{
			server.broadcastMessage(_("resetBalAll", start));
			for (String sUser : ess.getUserMap().getAllUniqueUsers())
			{
				final User player = ess.getUser(sUser);
				switch (cmd)
				{
				case GIVE:
					player.giveMoney(amount);
					break;

				case TAKE:
					if (player.canAfford(amount, false))
					{
						player.takeMoney(amount);
					}
					else
					{
						if (player.getMoney() > 0)
						{
							player.setMoney(0);
						}
					}
					break;

				case RESET:
					player.setMoney(startingBalance);
					break;

				case SET:
					boolean underMinimum = (player.getMoney() - amount) < min;
					player.setMoney(underMinimum ? min : amount);
					break;
				}
			}
		}
		else if (args[1].contentEquals("*"))
		{
			server.broadcastMessage(_("resetBal", start));
			for (Player onlinePlayer : server.getOnlinePlayers())
			{
				final User player = ess.getUser(onlinePlayer);
				switch (cmd)
				{
				case GIVE:
					player.giveMoney(amount);
					break;

				case TAKE:
					if (player.canAfford(amount))
					{
						player.takeMoney(amount);
					}
					else
					{
						if (player.getMoney() > 0)
						{
							player.setMoney(0);
						}
					}
					break;

				case RESET:
					player.setMoney(startingBalance);
					break;

				case SET:
					boolean underMinimum = (player.getMoney() - amount) < min;
					player.setMoney(underMinimum ? min : amount);
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
				if (player.canAfford(amount))
				{
					player.takeMoney(amount);
				}
				else
				{
					if (player.getMoney() > 0)
					{
						player.setMoney(0);
					}
				}
				break;

			case RESET:
				player.setMoney(startingBalance);
				break;

			case SET:
				boolean underMinimum = (player.getMoney() - amount) < min;
				player.setMoney(underMinimum ? min : amount);
				break;
			}
		}
	}


	private enum EcoCommands
	{
		GIVE, TAKE, RESET, SET
	}
}
