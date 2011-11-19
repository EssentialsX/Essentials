package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
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

		double amount = Double.parseDouble(args[1].replaceAll("[^0-9\\.]", ""));

		Boolean foundUser = false;
		for (Player p : server.matchPlayer(args[0]))
		{
			User u = ess.getUser(p);
			if (u.isHidden())
			{
				continue;
			}
			user.payUser(u, amount);
			foundUser = true;
		}
		
		if(foundUser == false) {
			throw new NoSuchFieldException(Util.i18n("playerNotFound"));
		}
	}
}
