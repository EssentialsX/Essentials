package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TreeType;


public class Commandtree extends EssentialsCommand
{
	public Commandtree()
	{
		super("tree");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		TreeType tree;
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		else if (args[0].equalsIgnoreCase("birch"))
		{
			tree = TreeType.BIRCH;
		}
		else if (args[0].equalsIgnoreCase("redwood"))
		{
			tree = TreeType.REDWOOD;
		}
		else if (args[0].equalsIgnoreCase("tree"))
		{
			tree = TreeType.TREE;
		}
		else if (args[0].equalsIgnoreCase("redmushroom"))
		{
			tree = TreeType.RED_MUSHROOM;
		}
		else if (args[0].equalsIgnoreCase("brownmushroom"))
		{
			tree = TreeType.BROWN_MUSHROOM;
		}
		else if (args[0].equalsIgnoreCase("jungle"))
		{
			tree = TreeType.SMALL_JUNGLE;
		}
		else if (args[0].equalsIgnoreCase("junglebush"))
		{
			tree = TreeType.JUNGLE_BUSH;
					}
		else if (args[0].equalsIgnoreCase("swamp"))
		{
			tree = TreeType.SWAMP;
		}
		else
		{
			throw new NotEnoughArgumentsException();
		}

		final Location loc = Util.getTarget(user);
		final Location safeLocation = Util.getSafeDestination(loc);
		final boolean success = user.getWorld().generateTree(safeLocation, tree);
		if (success)
		{
			user.sendMessage(_("treeSpawned"));
		}
		else
		{
			user.sendMessage(_("treeFailure"));
		}
	}
}
