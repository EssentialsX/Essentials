package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IUser;
import org.bukkit.Server;
import org.bukkit.entity.Player;


public class Commandpay extends EssentialsCommand
{
	public Commandpay()
	{
		super("pay");
	}

	@Override
	public void run(final Server server, final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		double amount = Double.parseDouble(args[1].replaceAll("[^0-9\\.]", ""));

		Boolean foundUser = false;
		for (Player p : server.matchPlayer(args[0]))
		{
			IUser u = ess.getUser(p);
			if (u.isHidden())
			{
				continue;
			}
			user.payUser(u, amount);
			Trade.log("Command", "Pay", "Player", user.getName(), new Trade(amount, ess), u.getName(), new Trade(amount, ess), user.getLocation(), ess);
			foundUser = true;
		}

		if (foundUser == false)
		{
			throw new NoSuchFieldException(_("playerNotFound"));
		}
	}
}
