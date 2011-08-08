package com.earth2me.essentials.commands;

import com.earth2me.essentials.DescParseTickFormat;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class Commandptime extends EssentialsCommand
{
	// TODO: I suggest that the chat colors be centralized in the config file.
	public static final ChatColor colorDefault = ChatColor.YELLOW;
	public static final ChatColor colorChrome = ChatColor.GOLD;
	public static final ChatColor colorLogo = ChatColor.GREEN;
	public static final ChatColor colorHighlight1 = ChatColor.AQUA;
	public static final ChatColor colorBad = ChatColor.RED;

	public Commandptime()
	{
		super("ptime");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		// Which Players(s) / Users(s) are we interested in?
		String userSelector = null;
		if (args.length == 2)
		{
			userSelector = args[1];
		}
		if (args.length == 3)
		{
			userSelector = args[2];
		}
		Set<User> users = getUsers(server, sender, userSelector);

		// If no arguments we are reading the time
		if (args.length == 0)
		{
			getUsersTime(sender, users);
			return;
		}

		User user = ess.getUser(sender);
		if (user != null && !user.isAuthorized("essentials.ptime.others"))
		{
			// TODO should not be hardcoded !!
			throw new Exception(colorBad + "You are not authorized to set others PlayerTime");
		}

		Long ticks;
		// Parse the target time int ticks from args[0]
		String timeParam = args.length == 2 ? args[0] : args[0] + args[1];
		if (DescParseTickFormat.meansReset(timeParam))
		{
			ticks = null;
		}
		else
		{
			try
			{
				ticks = DescParseTickFormat.parse(timeParam);
			}
			catch (NumberFormatException e)
			{
				throw new NotEnoughArgumentsException();
			}
		}

		setUsersTime(sender, users, ticks);
	}

	/**
	 * Used to get the time and inform
	 */
	private void getUsersTime(final CommandSender sender, final Collection<User> users)
	{
		if (users.size() == 1)
		{
			final User user = users.iterator().next();

			if (user.isPlayerTimeRelative())
			{
				sender.sendMessage(colorDefault + user.getName() + "'s time is normal. Time is the same as on the server.");
			}
			else
			{
				sender.sendMessage(colorDefault + user.getName() + "'s time is fixed to: " + DescParseTickFormat.format(user.getPlayerTime()));
			}
			return;
		}

		sender.sendMessage(colorDefault + "These players have fixed time:");

		for (User user : users)
		{
			if (!user.isPlayerTimeRelative())
			{
				sender.sendMessage(colorDefault + user.getName() + ": " + DescParseTickFormat.format(user.getPlayerTime()));
			}
		}
		return;
	}

	/**
	 * Used to set the time and inform of the change
	 */
	private void setUsersTime(final CommandSender sender, final Collection<User> users, final Long ticks)
	{
		// Update the time
		if (ticks == null)
		{
			// Reset
			for (User user : users)
			{
				user.resetPlayerTime();
			}
		}
		else
		{
			// Set
			for (User user : users)
			{
				long time = user.getPlayerTime();
				time -= time % 24000;
				final World world = user.getWorld();
				user.setPlayerTime(time + 24000 + ticks - world.getTime(), true);
			}
		}


		// Inform the sender of the change
		sender.sendMessage("");
		final StringBuilder msg = new StringBuilder();
		if (ticks == null)
		{
			sender.sendMessage(colorDefault + "The players time was reset for:");
		}
		else
		{
			sender.sendMessage(colorDefault + "The players time was fixed to:");
			sender.sendMessage(DescParseTickFormat.format(ticks));
			msg.append(colorDefault);
			msg.append("For: ");
		}

		boolean first = true;
		for (User user : users)
		{
			if (!first)
			{
				msg.append(colorDefault);
				msg.append(", ");
			}
			else
			{
				first = false;
			}

			msg.append(colorHighlight1);
			msg.append(user.getName());
		}

		sender.sendMessage(msg.toString());
	}

	/**
	 * Used to parse an argument of the type "users(s) selector"
	 */
	private Set<User> getUsers(final Server server, final CommandSender sender, final String selector) throws Exception
	{
		final Set<User> users = new TreeSet<User>(new UserNameComparator());
		// If there is no selector we want the sender itself. Or all users if sender isn't a user.
		if (selector == null)
		{
			final User user = ess.getUser(sender);
			if (user == null)
			{
				for (Player player : server.getOnlinePlayers())
				{
					users.add(ess.getUser(player));
				}
			}
			else
			{
				users.add(user);
			}
			return users;
		}

		// Try to find the user with name = selector
		User user = null;
		final List<Player> matchedPlayers = server.matchPlayer(selector);
		if (!matchedPlayers.isEmpty())
		{
			user = ess.getUser(matchedPlayers.get(0));
		}

		if (user != null)
		{
			users.add(user);
		}
		// If that fails, Is the argument something like "*" or "all"?
		else if (selector.equalsIgnoreCase("*") || selector.equalsIgnoreCase("all"))
		{
			for (Player player : server.getOnlinePlayers())
			{
				users.add(ess.getUser(player));
			}
		}
		// We failed to understand the world target...
		else
		{
			throw new Exception("Could not find the player(s) \"" + selector + "\"");
		}

		return users;
	}
}


class UserNameComparator implements Comparator<User>
{
	public int compare(User a, User b)
	{
		return a.getName().compareTo(b.getName());
	}
}
