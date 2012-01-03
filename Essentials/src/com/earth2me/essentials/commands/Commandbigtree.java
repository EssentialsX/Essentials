package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IUser;
import org.bukkit.Location;
import org.bukkit.TreeType;


public class Commandbigtree extends EssentialsCommand
{
	@Override
	public void run(final IUser user,  final String[] args) throws Exception
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
			user.sendMessage(_("bigTreeSuccess"));
		}
		else
		{
			throw new Exception(_("bigTreeFailure"));
		}
	}
}
