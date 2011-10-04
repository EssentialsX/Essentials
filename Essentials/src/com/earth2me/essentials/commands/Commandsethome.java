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
			//Allowing both formats /sethome khobbits house | /sethome khobbits:house
			final String[] nameParts = args[0].split(":");
			if (nameParts[0].length() != args[0].length())
			{
				args = nameParts;
			}

			if (args.length < 2)
			{
				if (user.isAuthorized("essentials.sethome.multiple"))
				{
					if ((user.isAuthorized("essentials.sethome.multiple.unlimited")) || (user.getHomes().size() < ess.getSettings().getHomeLimit(user))
						|| (user.getHomes().contains(args[0].toLowerCase())))
					{
						user.setHome(args[0].toLowerCase());
					}
					else
					{
						throw new Exception(Util.format("maxHomes", ess.getSettings().getHomeLimit(user)));
					}

				}
				else {
					throw new Exception(Util.format("maxHomes", 1));
				}
			}
			else
			{
				if (user.isAuthorized("essentials.sethome.others"))
				{
					User usersHome = ess.getUser(ess.getServer().getPlayer(args[0]));
					if (usersHome == null)
					{
						usersHome = ess.getOfflineUser(args[0]);
					}
					if (usersHome == null)
					{
						throw new Exception(Util.i18n("playerNotFound"));
					}
					String name = args[1].toLowerCase();
					if (!user.isAuthorized("essentials.sethome.multiple"))
					{
						name = "home";
					}
					usersHome.setHome(name, user.getLocation());
				}
			}
		}
		else
		{
			user.setHome();
		}
		user.sendMessage(Util.i18n("homeSet"));

	}
}
