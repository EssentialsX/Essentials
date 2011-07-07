package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandsethome extends EssentialsCommand
{
	public Commandsethome()
	{
		super("sethome");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0)
		{
			if (args.length < 2)
			{
				user.setHome(args[0].equalsIgnoreCase("default"));
			}
			else
			{
				if (user.isAuthorized("essentials.sethome.others"))
				{
					User usersHome = ess.getUser(ess.getServer().getPlayer(args[0]));
					usersHome.setHome(args[1].equalsIgnoreCase("default"));
				}
			}
		}
		else
		{
			user.setHome(false);
		}
		charge(user);
		user.sendMessage(Util.i18n("homeSet"));

	}
}
