package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import java.math.BigDecimal;
import org.bukkit.Server;


public class Commandbalance extends EssentialsCommand
{
	public Commandbalance()
	{
		super("balance");
	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User target = getPlayer(server, args, 0, true, true);
		sender.sendMessage(tl("balanceOther", target.isHidden() ? target.getName() : target.getDisplayName(), NumberUtil.displayCurrency(target.getMoney(), ess)));
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length == 1 && user.isAuthorized("essentials.balance.others"))
		{
			final User target = getPlayer(server, args, 0, true, true);
			final BigDecimal bal = target.getMoney();
			user.sendMessage(tl("balanceOther", target.isHidden() ? target.getName() : target.getDisplayName(), NumberUtil.displayCurrency(bal, ess)));
		}
		else if (args.length < 2)
		{
			final BigDecimal bal = user.getMoney();
			user.sendMessage(tl("balance", NumberUtil.displayCurrency(bal, ess)));
		}
		else
		{
			throw new NotEnoughArgumentsException();
		}
	}
}
