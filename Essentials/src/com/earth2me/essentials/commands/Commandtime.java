package com.earth2me.essentials.commands;

import com.earth2me.essentials.DescParseTickFormat;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.*;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;


public class Commandtime extends EssentialsCommand
{
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
		final Set<World> worlds = getWorlds(server, sender, worldSelector);

		// If no arguments we are reading the time
		if (args.length == 0)
		{
			getWorldsTime(sender, worlds);
			return;
		}

		final User user = ess.getUser(sender);
		if (user != null && !user.isAuthorized("essentials.time.set"))
		{
			user.sendMessage(Util.i18n("timeSetPermission"));
			return;
		}

		// Parse the target time int ticks from args[0]
		long ticks;
		try
		{
			ticks = DescParseTickFormat.parse(args[0]);
		}
		catch (NumberFormatException e)
		{
			throw new NotEnoughArgumentsException();
		}

		setWorldsTime(sender, worlds, ticks);
	}

	/**
	 * Used to get the time and inform
	 */
	private void getWorldsTime(CommandSender sender, Collection<World> worlds)
	{
		if (worlds.size() == 1)
		{
			Iterator<World> iter = worlds.iterator();
			sender.sendMessage(DescParseTickFormat.format(iter.next().getTime()));
			return;
		}

		for (World world : worlds)
		{
			sender.sendMessage(Util.format("timeCurrentWorld", world.getName(), DescParseTickFormat.format(world.getTime())));
		}
	}

	/**
	 * Used to set the time and inform of the change
	 */
	private void setWorldsTime(CommandSender sender, Collection<World> worlds, long ticks)
	{
		// Update the time
		for (World world : worlds)
		{
			long time = world.getTime();
			time -= time % 24000;
			world.setTime(time + 24000 + ticks);
		}

		// Inform the sender of the change
		//sender.sendMessage("");

		StringBuilder msg = new StringBuilder();
		boolean first = true;
		for (World world : worlds)
		{
			if (msg.length() > 0)
			{
				msg.append(", ");
			}

			msg.append(world.getName());
		}

		sender.sendMessage(Util.format("timeWorldSet", DescParseTickFormat.format(ticks), msg.toString()));
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
			throw new Exception(Util.i18n("invalidWorld"));
		}

		return worlds;
	}
}


class WorldNameComparator implements Comparator<World>
{
	@Override
	public int compare(World a, World b)
	{
		return a.getName().compareTo(b.getName());
	}
}
