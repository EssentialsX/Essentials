package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
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
		Double broadcast = null;
		Double broadcastAll = null;
		final double startingBalance = (double)ess.getSettings().getStartingBalance();
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

		final double minBalance = ess.getSettings().getMinMoney();

		if (args[1].contentEquals("**"))
		{
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
					broadcastAll = startingBalance;
					break;

				case SET:
					boolean underMinimum = (player.getMoney() - amount) < minBalance;
					player.setMoney(underMinimum ? minBalance : amount);
					broadcastAll = underMinimum ? minBalance : amount;
					break;
				}
			}
		}
		else if (args[1].contentEquals("*"))
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
					broadcast = startingBalance;
					break;

				case SET:
					boolean underMinimum = (player.getMoney() - amount) < minBalance;
					player.setMoney(underMinimum ? minBalance : amount);
					broadcast = underMinimum ? minBalance : amount;
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
				boolean underMinimum = (player.getMoney() - amount) < minBalance;
				player.setMoney(underMinimum ? minBalance : amount);
				break;
			}
		}

		if (broadcast != null)
		{
			server.broadcastMessage(_("resetBal", Util.formatAsCurrency(broadcast)));
		}
		if (broadcastAll != null)
		{
			server.broadcastMessage(_("resetBalAll", Util.formatAsCurrency(broadcastAll)));
		}
	}


	private enum EcoCommands
	{
		GIVE, TAKE, RESET, SET
	}
}
