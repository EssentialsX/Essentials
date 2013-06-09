package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import java.math.BigDecimal;
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
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		Commandeco.EcoCommands cmd;
		BigDecimal startingBalance = ess.getSettings().getStartingBalance();
		BigDecimal amount;
		BigDecimal broadcast = null;
		BigDecimal broadcastAll = null;
		try
		{
			cmd = Commandeco.EcoCommands.valueOf(args[0].toUpperCase(Locale.ENGLISH));
			amount = (cmd == Commandeco.EcoCommands.RESET) ? startingBalance : new BigDecimal(args[2].replaceAll("[^0-9\\.]", ""));
		}
		catch (Exception ex)
		{
			throw new NotEnoughArgumentsException(ex);
		}

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
					take(amount, player, null);
					break;

				case RESET:
				case SET:
					set(amount, player, null);
					broadcastAll = amount;
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
					take(amount, player, null);
					break;

				case RESET:
				case SET:
					set(amount, player, null);
					broadcast = amount;
					break;
				}
			}
		}
		else
		{
			final User player = getPlayer(server, args, 1, true, true);
			switch (cmd)
			{
			case GIVE:
				player.giveMoney(amount, sender);
				break;

			case TAKE:
				take(amount, player, sender);
				break;

			case RESET:
			case SET:
				set(amount, player, sender);
				break;
			}
		}

		if (broadcast != null)
		{
			server.broadcastMessage(_("resetBal", NumberUtil.displayCurrency(broadcast, ess)));
		}
		if (broadcastAll != null)
		{
			server.broadcastMessage(_("resetBalAll", NumberUtil.displayCurrency(broadcastAll, ess)));
		}
	}

	private void take(BigDecimal amount, final User player, final CommandSender sender) throws Exception
	{
		BigDecimal money = player.getMoney();
		BigDecimal minBalance = ess.getSettings().getMinMoney();
		if (money.subtract(amount).compareTo(minBalance) > 0)
		{
			player.takeMoney(amount, sender);
		}
		else if (sender == null)
		{
			player.setMoney(minBalance);
			player.sendMessage(_("takenFromAccount", NumberUtil.displayCurrency(player.getMoney(), ess)));
		}
		else
		{
			throw new Exception(_("insufficientFunds"));
		}
	}

	private void set(BigDecimal amount, final User player, final CommandSender sender)
	{
		BigDecimal minBalance = ess.getSettings().getMinMoney();
		boolean underMinimum = (amount.compareTo(minBalance) < 0);
		player.setMoney(underMinimum ? minBalance : amount);
		player.sendMessage(_("setBal", NumberUtil.displayCurrency(player.getMoney(), ess)));
		if (sender != null)
		{
			sender.sendMessage(_("setBalOthers", player.getDisplayName(), NumberUtil.displayCurrency(player.getMoney(), ess)));
		}
	}


	private enum EcoCommands
	{
		GIVE, TAKE, SET, RESET
	}
}