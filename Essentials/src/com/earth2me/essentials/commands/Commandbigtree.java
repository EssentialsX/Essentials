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
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		Object tree = new Object();
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

		double x = user.getLocation().getX();
		double y = user.getLocation().getY();
		double z = user.getLocation().getZ();

		// offset tree in direction player is facing
		int r = (int)user.getCorrectedYaw();
		if (r < 68 || r > 292)			// north
		{
			x -= 3.0D;
		}			
		else if (r > 112 && r < 248)	// south
		{
			x += 3.0D;
		}		
		if (r > 22 && r < 158)			// east
		{
			z -= 3.0D;
		}			
		else if (r > 202 && r < 338)	// west
		{
			z += 3.0D;
		}		

		Location safeLocation = Util.getSafeDestination(new Location(user.getWorld(), x, y, z));
		boolean success = user.getWorld().generateTree(safeLocation, (TreeType)tree);
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
