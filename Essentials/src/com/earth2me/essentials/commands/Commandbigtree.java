package com.earth2me.essentials.commands;

import com.earth2me.essentials.TargetBlock;
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
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
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

		final int[] ignore =
		{
			8, 9
		};		
		final Location loc = (new TargetBlock(user, 300, 0.2, ignore)).getTargetBlock().getLocation();
		final Location safeLocation = Util.getSafeDestination(loc);
		final boolean success = user.getWorld().generateTree(safeLocation, (TreeType)tree);
		if (success)
		{
			charge(user);
			user.sendMessage(Util.i18n("bigTreeSuccess"));
		}
		else
		{
			user.sendMessage(Util.i18n("bigTreeFailure"));
		}
	}
}
