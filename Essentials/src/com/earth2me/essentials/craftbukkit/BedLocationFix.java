package com.earth2me.essentials.craftbukkit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;


public class BedLocationFix
{
	/*
	 * Adds missing null pointer check to getHandle().getBed()
	 */
	public static Location getBedSpawnLocation(final Player player)
	{
		final CraftPlayer cplayer = (CraftPlayer)player;
		final World world = player.getServer().getWorld(cplayer.getHandle().spawnWorld);
		if (world != null && cplayer.getHandle().getBed() != null)
		{
			return new Location(world, cplayer.getHandle().getBed().x, cplayer.getHandle().getBed().y, cplayer.getHandle().getBed().z);
		}
		else
		{
			return null;
		}
	}
}
