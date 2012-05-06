package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.Locale;
import org.bukkit.Server;


public class Commandsethome extends EssentialsCommand
{
	public Commandsethome()
	{
		super("sethome");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, String[] args) throws Exception
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
					if ("bed".equals(args[0].toLowerCase(Locale.ENGLISH)))
					{
						throw new NotEnoughArgumentsException();
					}
					if ((user.isAuthorized("essentials.sethome.multiple.unlimited")) || (user.getHomeCount() < ess.getSettings().getHomeLimit(user))
						|| (user.getHomes().contains(args[0].toLowerCase(Locale.ENGLISH))))
					{
						user.setHome(args[0].toLowerCase(Locale.ENGLISH));
					}
					else
					{
						throw new Exception(_("maxHomes", ess.getSettings().getHomeLimit(user)));
					}

				}
				else
				{
					throw new Exception(_("maxHomes", 1));
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
						throw new Exception(_("playerNotFound"));
					}
					String name = args[1].toLowerCase(Locale.ENGLISH);
					if (!user.isAuthorized("essentials.sethome.multiple"))
					{
						name = "home";
					}
					if ("bed".equals(name.toLowerCase(Locale.ENGLISH)))
					{
						throw new NotEnoughArgumentsException();
					}
					usersHome.setHome(name, user.getLocation());
				}
			}
		}
		else
		{
			user.setHome();
		}
		user.sendMessage(_("homeSet", user.getLocation().getWorld().getName(), user.getLocation().getBlockX(), user.getLocation().getBlockY(), user.getLocation().getBlockZ()));

	}
}
