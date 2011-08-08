package com.earth2me.essentials.commands;

import com.earth2me.essentials.DescParseTickFormat;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import java.util.*;
import org.bukkit.ChatColor;
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
		Set<User> users = getUsers(server, sender, userSelector);
		
		// If no arguments we are reading the time
		if (args.length == 0)
		{
			getUsersTime(sender, users);
			return;
		}

		User user = ess.getUser(sender);
		if ( user != null && ! user.isAuthorized("essentials.ptime.others"))
		{
			// TODO should not be hardcoded !!
			sender.sendMessage(colorBad + "You are not authorized to set others PlayerTime");
			return; // TODO: How to not just die silently? in a good way??
		}
		
		Long ticks;
		// Parse the target time int ticks from args[0]
		if (DescParseTickFormat.meansReset(args[0]))
		{
			ticks = null;
		}
		else
		{
			try
			{
				ticks = DescParseTickFormat.parse(args[0]);
			}
			catch (NumberFormatException e)
			{
				// TODO: Display an error with help included... on how to specify the time
				sender.sendMessage(colorBad + "Unknown time descriptor... brlalidididiablidadadibibibiiba!! TODO");
				return;
			}
		}
		
		setUsersTime(sender, users, ticks);
	}
	
	
	/**
	 * Used to get the time and inform
	 */
	private void getUsersTime(CommandSender sender, Collection<User> users)
	{
		if (users.size() == 1)
		{
			Iterator<User> iter = users.iterator();
			User user = iter.next();
			
			if (user.isPlayerTimeRelative())
			{
				sender.sendMessage(colorDefault + user.getName() + "'s time is normal. Time is the same as on the server.");
			}
			else
			{
				sender.sendMessage(colorDefault + user.getName() + "'s time is fixed to: "+DescParseTickFormat.format(user.getPlayerTime()));
			}
			return;
		}
		
		sender.sendMessage(colorDefault + "These players have fixed time:");
		
		for (User user : users)		
		{
			if ( ! user.isPlayerTimeRelative())
			{
				sender.sendMessage(colorDefault + user.getName() + ": "+DescParseTickFormat.format(user.getPlayerTime()));
			}
		}
		return;
	}
	
	/**
	 * Used to set the time and inform of the change
	 */
	private void setUsersTime(CommandSender sender, Collection<User> users, Long ticks)
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
				user.setPlayerTime(ticks, false);
			}
		}
		
		
		// Inform the sender of the change
		sender.sendMessage("");
		StringBuilder msg = new StringBuilder();
		if (ticks == null)
		{
			sender.sendMessage(colorDefault + "The PlayerTime was reset for:");
		}
		else
		{
			sender.sendMessage(colorDefault + "The PlayerTime was fixed to:");
			sender.sendMessage(DescParseTickFormat.format(ticks));
			msg.append(colorDefault);
			msg.append("For: ");
		}
		
		boolean first = true;
		for (User user : users)
		{
			if ( ! first)
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
	private Set<User> getUsers(Server server, CommandSender sender, String selector) throws Exception
	{
		Set<User> users = new TreeSet<User>(new UserNameComparator());
		Player[] players;
		// If there is no selector we want the sender itself. Or all users if sender isn't a user.
		if (selector == null)
		{
			User user = ess.getUser(sender);
			if (user == null)
			{
				users.addAll(ess.getAllOnlineUsers().values());
			}
			else
			{
				users.add(user);
			}
			return users;
		}
		
		// Try to find the user with name = selector
		User user = null;
		List<Player> matchedPlayers = server.matchPlayer(selector);
		if (matchedPlayers.size() > 0)
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
			users.addAll(ess.getAllOnlineUsers().values());
		}
		// We failed to understand the world target...
		else
		{
			throw new Exception("Could not find the player(s) \""+selector+"\"");
		}
		
		return users;
	}
}

class UserNameComparator implements Comparator<User> {
	public int compare(User a, User b)
	{
		return a.getName().compareTo(b.getName());
	}
}
