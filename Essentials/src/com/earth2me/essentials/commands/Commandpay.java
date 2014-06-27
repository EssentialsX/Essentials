package com.earth2me.essentials.commands;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import java.math.BigDecimal;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Server;


public class Commandpay extends EssentialsLoopCommand
{
	BigDecimal amount;

	public Commandpay()
	{
		super("pay");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		String stringAmount = args[1].replaceAll("[^0-9\\.]", "");

		if (stringAmount.length() < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		amount = new BigDecimal(stringAmount);
		loopOnlinePlayers(server, user.getSource(), false, user.isAuthorized("essentials.pay.multiple"), args[0], args);
	}

	@Override
	protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args) throws ChargeException
	{
		User user = ess.getUser(sender.getPlayer());
		try
		{
			user.payUser(player, amount);
			Trade.log("Command", "Pay", "Player", user.getName(), new Trade(amount, ess), player.getName(), new Trade(amount, ess), user.getLocation(), ess);
		}
		catch (MaxMoneyException ex)
		{
			sender.sendMessage(tl("maxMoney"));
		}
	}
}
