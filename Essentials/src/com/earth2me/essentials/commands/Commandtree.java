package com.earth2me.essentials.commands;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TreeType;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandtree extends EssentialsCommand
{
	public Commandtree()
	{
		super("tree");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
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
		else
		{
			throw new NotEnoughArgumentsException();
		}
		
		final int[] ignore =
		{
			8, 9
		};		
		final Location loc = Util.getTarget(user);
		final Location safeLocation = Util.getSafeDestination(loc);
		final boolean success = user.getWorld().generateTree(safeLocation, tree);
		if (success)
		{
			user.sendMessage(Util.i18n("treeSpawned"));
		}
		else
		{
			user.sendMessage(Util.i18n("treeFailure"));
		}
	}
}
