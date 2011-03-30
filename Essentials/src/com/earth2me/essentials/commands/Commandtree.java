package com.earth2me.essentials.commands;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TreeType;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

public class Commandtree extends EssentialsCommand
{
	public Commandtree()
	{
		super("tree");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		Object tree = new Object();
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /tree [tree|birch|redwood]");
			return;
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
		   user.sendMessage("§cUsage: /tree [tree|birch|redwood]");
		   return;
		}

		double x = user.getLocation().getX();
		double y = user.getLocation().getY();
		double z = user.getLocation().getZ();

		// offset tree in direction player is facing
		int r = (int)user.getCorrectedYaw();
		if (r < 68 || r > 292) x -= 3.0D;			// north
		else if (r > 112 && r < 248) x += 3.0D;		// south
		if (r > 22 && r < 158) z -= 3.0D;			// east
		else if (r > 202 && r < 338) z += 3.0D;		// west

		Location safeLocation = user.getSafeDestination(new Location(user.getWorld(), x, y, z));
		boolean success = user.getWorld().generateTree(safeLocation, (TreeType)tree);
		if (success)
		{
			user.charge(this);
			user.sendMessage("§7Tree spawned.");
		}
		else
			user.sendMessage("§cTree generation failure. Try again on grass or dirt.");
	}
}
