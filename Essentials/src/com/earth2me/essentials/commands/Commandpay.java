package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;


public class Commandpay extends EssentialsCommand
{
	public Commandpay()
	{
		super("pay");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		
		//TODO: TL this
		if (args[0].trim().length() < 2)
		{
			throw new NotEnoughArgumentsException("You need to specify a player to pay.");
		}

		double amount = Double.parseDouble(args[1].replaceAll("[^0-9\\.]", ""));

		boolean foundUser = false;
		for (Player p : server.matchPlayer(args[0]))
		{
			User u = ess.getUser(p);
			if (u.isHidden())
			{
				continue;
			}
			user.payUser(u, amount);
			Trade.log("Command", "Pay", "Player", user.getName(), new Trade(amount, ess), u.getName(), new Trade(amount, ess), user.getLocation(), ess);
			foundUser = true;
		}

		if (!foundUser)
		{
			throw new NotEnoughArgumentsException(_("playerNotFound"));
		}
	}
}
