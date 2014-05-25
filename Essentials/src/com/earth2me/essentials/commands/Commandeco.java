package com.earth2me.essentials.commands;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import java.math.BigDecimal;
import java.util.Locale;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Server;


public class Commandeco extends EssentialsLoopCommand
{
	Commandeco.EcoCommands cmd;
	BigDecimal amount;

	public Commandeco()
	{
		super("eco");
	}

	@Override
	public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		BigDecimal startingBalance = ess.getSettings().getStartingBalance();

		try
		{
			cmd = Commandeco.EcoCommands.valueOf(args[0].toUpperCase(Locale.ENGLISH));
			amount = (cmd == Commandeco.EcoCommands.RESET) ? startingBalance : new BigDecimal(args[2].replaceAll("[^0-9\\.]", ""));
		}
		catch (Exception ex)
		{
			throw new NotEnoughArgumentsException(ex);
		}

		loopOfflinePlayers(server, sender, false, true, args[1], args);

		if (cmd == Commandeco.EcoCommands.RESET || cmd == Commandeco.EcoCommands.SET)
		{
			if (args[1].contentEquals("**"))
			{
				server.broadcastMessage(tl("resetBalAll", NumberUtil.displayCurrency(amount, ess)));
			}
			else if (args[1].contentEquals("*"))
			{
				server.broadcastMessage(tl("resetBal", NumberUtil.displayCurrency(amount, ess)));
			}
		}
	}

	@Override
	protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args) throws NotEnoughArgumentsException, ChargeException, MaxMoneyException
	{
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

	private void take(BigDecimal amount, final User player, final CommandSource sender) throws ChargeException
	{
		BigDecimal money = player.getMoney();
		BigDecimal minBalance = ess.getSettings().getMinMoney();
		if (money.subtract(amount).compareTo(minBalance) > 0)
		{
			player.takeMoney(amount, sender);
		}
		else if (sender == null)
		{
			try
			{
				player.setMoney(minBalance);
			}
			catch (MaxMoneyException ex)
			{
				// Take shouldn't be able to throw a max money exception
			}
			player.sendMessage(tl("takenFromAccount", NumberUtil.displayCurrency(player.getMoney(), ess)));
		}
		else
		{
			throw new ChargeException(tl("insufficientFunds"));
		}
	}

	private void set(BigDecimal amount, final User player, final CommandSource sender) throws MaxMoneyException
	{
		BigDecimal minBalance = ess.getSettings().getMinMoney();
		BigDecimal maxBalance = ess.getSettings().getMaxMoney();
		boolean underMinimum = (amount.compareTo(minBalance) < 0);
		boolean aboveMax = (amount.compareTo(maxBalance) > 0);
		player.setMoney(underMinimum ? minBalance : aboveMax ? maxBalance : amount);
		player.sendMessage(tl("setBal", NumberUtil.displayCurrency(player.getMoney(), ess)));
		if (sender != null)
		{
			sender.sendMessage(tl("setBalOthers", player.getDisplayName(), NumberUtil.displayCurrency(player.getMoney(), ess)));
		}
	}


	private enum EcoCommands
	{
		GIVE, TAKE, SET, RESET
	}
}