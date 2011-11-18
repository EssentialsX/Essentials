package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.TreeType;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Location;


public class Commandbigtree extends EssentialsCommand
{
	public Commandbigtree()
	{
		super("bigtree");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		TreeType tree;
		if (args.length > 0 && args[0].equalsIgnoreCase("redwood"))
		{
			tree = TreeType.TALL_REDWOOD;
		}
		else if (args.length > 0 && args[0].equalsIgnoreCase("tree"))
		{
			tree = TreeType.BIG_TREE;
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
			user.sendMessage(Util.i18n("bigTreeSuccess"));
		}
		else
		{
			throw new Exception(Util.i18n("bigTreeFailure"));
		}
	}
}
