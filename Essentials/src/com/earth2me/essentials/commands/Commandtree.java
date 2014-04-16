package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.LocationUtil;
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
		TreeType tree = TreeType.BIRCH;
		try // update check
		{
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
			else if (args[0].equalsIgnoreCase("acacia"))
			{
				tree = TreeType.ACACIA;
			}
			else if (args[0].equalsIgnoreCase("darkoak"))
			{
				tree = TreeType.DARK_OAK;
			}
			else
			{
				throw new NotEnoughArgumentsException();
			}
		}
		catch (java.lang.NoSuchFieldError e)
		{
			Essentials.wrongVersion();
		}

		final Location loc = LocationUtil.getTarget(user.getBase());
		final Location safeLocation = LocationUtil.getSafeDestination(loc);
		final boolean success = user.getWorld().generateTree(safeLocation, tree);
		if (success)
		{
			user.sendMessage(tl("treeSpawned"));
		}
		else
		{
			user.sendMessage(tl("treeFailure"));
		}
	}
}
