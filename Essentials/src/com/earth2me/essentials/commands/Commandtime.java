package com.earth2me.essentials.commands;

import com.earth2me.essentials.DescParseTickFormat;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import java.util.*;
import org.bukkit.ChatColor;


public class Commandtime extends EssentialsCommand
{
	// TODO: I suggest that the chat colors be centralized in the config file.
	public static final ChatColor colorDefault = ChatColor.YELLOW;
	public static final ChatColor colorChrome = ChatColor.GOLD;
	public static final ChatColor colorLogo = ChatColor.GREEN;
	public static final ChatColor colorHighlight1 = ChatColor.AQUA;
	public static final ChatColor colorBad = ChatColor.RED;
	
	public Commandtime()
	{
		super("time");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		// Which World(s) are we interested in?
		String worldSelector = null;
		if (args.length == 2)
		{
			worldSelector = args[1];
		}
		Set<World> worlds = getWorlds(server, sender, worldSelector);
		
		// If no arguments we are reading the time
		if (args.length == 0)
		{
			getWorldsTime(sender, worlds);
			return;
		}

		User user = ess.getUser(sender);
		if ( user != null && ! user.isAuthorized("essentials.time.set"))
		{
			// TODO should not be hardcoded !!
			sender.sendMessage(colorBad + "You are not authorized to set the time");
			return; // TODO: How to not just die silently? in a good way??
		}
		
		// Parse the target time int ticks from args[0]
		long ticks;
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

		setWorldsTime(sender, worlds, ticks);
	}
	
	/**
	 * Used to get the time and inform
	 */
	private void getWorldsTime(CommandSender sender, Collection<World> worlds)
	{
		// TODO do we need to check for the essentials.time permission? Or is that tested for us already.
		if (worlds.size() == 1)
		{
			Iterator<World> iter = worlds.iterator();
			sender.sendMessage(DescParseTickFormat.format(iter.next().getTime()));
			return;
		}
		
		for (World world : worlds)		
		{
			sender.sendMessage(colorDefault + world.getName()+": " + DescParseTickFormat.format(world.getTime()));
		}
		return;
	}
	
	/**
	 * Used to set the time and inform of the change
	 */
	private void setWorldsTime(CommandSender sender, Collection<World> worlds, long ticks)
	{
		// Update the time
		for (World world : worlds)
		{
			world.setTime(ticks);
		}
		
		// Inform the sender of the change
		sender.sendMessage("");
		sender.sendMessage(colorDefault + "The time was set to");
		sender.sendMessage(DescParseTickFormat.format(ticks));
		
		StringBuilder msg = new StringBuilder();
		msg.append(colorDefault);
		msg.append("In ");
		boolean first = true;
		for (World world : worlds)
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
			msg.append(world.getName());
		}
		
		sender.sendMessage(msg.toString());
	}
	
	/**
	 * Used to parse an argument of the type "world(s) selector"
	 */ 
	private Set<World> getWorlds(Server server, CommandSender sender, String selector) throws Exception
	{
		Set<World> worlds = new TreeSet<World>(new WorldNameComparator());
		
		// If there is no selector we want the world the user is currently in. Or all worlds if it isn't a user.
		if (selector == null)
		{
			User user = ess.getUser(sender);
			if (user == null)
			{
				worlds.addAll(server.getWorlds());
			}
			else
			{
				worlds.add(user.getWorld());
			}
			return worlds;
		}
		
		// Try to find the world with name = selector
		World world = server.getWorld(selector);
		if (world != null)
		{
			worlds.add(world);
		}
		// If that fails, Is the argument something like "*" or "all"?
		else if (selector.equalsIgnoreCase("*") || selector.equalsIgnoreCase("all"))
		{
			worlds.addAll(server.getWorlds());
		}
		// We failed to understand the world target...
		else
		{
			throw new Exception("Could not find the world(s) \""+selector+"\"");
		}
		
		return worlds;
	}
}



class WorldNameComparator implements Comparator<World> {
	public int compare(World a, World b)
	{
		return a.getName().compareTo(b.getName());
	}
}
