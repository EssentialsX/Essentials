package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.List;


public class Commandhome extends EssentialsCommand
{
	public Commandhome()
	{
		super("home");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		if (args.length == 0)
		{
			List<String> homes = user.getHomes();
			if (homes.isEmpty())
			{
				throw new Exception(Util.i18n("noHomeSet"));
			}
			else if (homes.size() == 1)
			{
				user.getTeleport().home(homes.get(0), charge);
			}
			else
			{
				//TODO: move to messages file
				user.sendMessage("Homes: " + homes.toString());
			}

		}
		else
		{
			User u;
			String homeName;
			String[] nameParts = args[0].split(":");
			if (nameParts.length == 1)
			{
				u = user;
				homeName = nameParts[0];
			}
			else
			{
				try
				{
					u = getPlayer(server, args, 0);
				}
				catch (NoSuchFieldException ex)
				{
					u = ess.getOfflineUser(args[0]);
				}
				if (u == null)
				{
					throw new Exception(Util.i18n("playerNotFound"));
				}
				homeName = nameParts[1];
			}
			user.getTeleport().home(u, homeName, charge);
		}
	}
}
