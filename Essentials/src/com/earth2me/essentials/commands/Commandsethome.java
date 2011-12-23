package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import java.util.HashMap;
import java.util.Locale;
import lombok.Cleanup;
import org.bukkit.Location;
import org.bukkit.Server;


public class Commandsethome extends EssentialsCommand
{
	public Commandsethome()
	{
		super("sethome");
	}

	@Override
	public void run(final Server server, final IUser user, final String commandLabel, String[] args) throws Exception
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
					if ((user.isAuthorized("essentials.sethome.multiple.unlimited")) || (user.getHomes().size() < ess.getGroups().getHomeLimit(user))
						|| (user.getHomes().contains(args[0].toLowerCase(Locale.ENGLISH))))
					{
						user.acquireWriteLock();
						if (user.getData().getHomes() == null)
						{
							user.getData().setHomes(new HashMap<String, Location>());
						}
						user.getData().getHomes().put(args[0].toLowerCase(Locale.ENGLISH), user.getLocation());
					}
					else
					{
						throw new Exception(_("maxHomes", ess.getGroups().getHomeLimit(user)));
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
					@Cleanup
					IUser usersHome = ess.getUser(ess.getServer().getPlayer(args[0]));
					if (usersHome == null)
					{
						throw new Exception(_("playerNotFound"));
					}
					String name = args[1].toLowerCase(Locale.ENGLISH);
					if (!user.isAuthorized("essentials.sethome.multiple"))
					{
						name = "home";
					}

					usersHome.acquireWriteLock();
					if (usersHome.getData().getHomes() == null)
					{
						usersHome.getData().setHomes(new HashMap<String, Location>());
					}
					usersHome.getData().getHomes().put(name, user.getLocation());
				}
			}
		}
		else
		{
			user.acquireWriteLock();
			if (user.getData().getHomes() == null)
			{
				user.getData().setHomes(new HashMap<String, Location>());
			}
			user.getData().getHomes().put("home", user.getLocation());
		}
		user.sendMessage(_("homeSet"));

	}
}
