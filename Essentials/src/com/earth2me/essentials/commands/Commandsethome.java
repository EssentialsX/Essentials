package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
import java.util.HashMap;
import java.util.Locale;
import lombok.Cleanup;
import org.bukkit.Location;


public class Commandsethome extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String commandLabel, String[] args) throws Exception
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
				if (Permissions.SETHOME_MULTIPLE.isAuthorized(user))
				{
					if ("bed".equals(args[0].toLowerCase(Locale.ENGLISH))) {
						throw new NotEnoughArgumentsException();
					}
					if ((user.getHomes().size() < ess.getGroups().getHomeLimit(user))
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
				if (Permissions.SETHOME_OTHERS.isAuthorized(user))
				{
					@Cleanup
					IUser usersHome = ess.getUser(ess.getServer().getPlayer(args[0]));
					if (usersHome == null)
					{
						throw new Exception(_("playerNotFound"));
					}
					String name = args[1].toLowerCase(Locale.ENGLISH);
					if (!Permissions.SETHOME_MULTIPLE.isAuthorized(user))
					{
						name = "home";
					}
					if ("bed".equals(name.toLowerCase(Locale.ENGLISH))) {
						throw new NotEnoughArgumentsException();
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
