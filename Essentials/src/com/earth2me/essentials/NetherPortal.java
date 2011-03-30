/**
 * @author SpaceManiac
 * @licent MIT
 * @origin https://github.com/SpaceManiac/Nether/blob/master/org/innectis/Nether/NetherPortal.java
 */
package com.earth2me.essentials;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NetherPortal
{
	private Block block;

	public NetherPortal(Block b)
	{
		block = b;
	}

	public Block getBlock()
	{
		return block;
	}

	public void setBlock(Block b)
	{
		block = b;
	}

	// Return a random spawnable location
	public Location getSpawn()
	{
		if (block.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ()).getType().equals(Material.PORTAL)
			|| block.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ()).getType().equals(Material.PORTAL))
		{
		// portal is in X direction
			return new Location(block.getWorld(), block.getX() + 1,
								block.getY(), block.getZ() + 1.5 - 2 * Math.round(Math.random()));
		}
		else
		{
		// portal is in Z direction
			return new Location(block.getWorld(), block.getX() + 1.5 - 2 * Math.round(Math.random()),
								block.getY(), block.getZ() + 1);
		}
	}

	// ==============================
	// Find a nearby portal within 16 blocks of the given block
	// Not guaranteed to be the nearest
	public static NetherPortal findPortal(Block dest)
	{
		World world = dest.getWorld();

		// Get list of columns in a circle around the block
		ArrayList<Block> columns = new ArrayList<Block>();
		for (int x = dest.getX() - 16; x <= dest.getX() + 16; ++x)
		{
			for (int z = dest.getZ() - 16; z <= dest.getZ() + 16; ++z)
			{
				int dx = dest.getX() - x, dz = dest.getZ() - z;
				if (dx * dx + dz * dz <= 256)
				{
					columns.add(world.getBlockAt(x, 0, z));
				}
			}
		}

		// For each column try to find a portal block
		for (Block col : columns)
		{
			for (int y = 127; y >= 0; --y)
			{
				Block b = world.getBlockAt(col.getX(), y, col.getZ());
				if (b.getType().equals(Material.PORTAL) && Math.abs(dest.getY() - y) <= 16)
				{
					// Huzzah!
					return new NetherPortal(b);
				}
			}
		}

		// Nope!
		return null;
	}

	// Create a new portal at the specified block, fudging position if needed
	// Will occasionally end up making portals in bad places, but let's hope not
	public static NetherPortal createPortal(Block dest)
	{
		World world = dest.getWorld();

		// Try not to spawn within water or lava
		Material m = dest.getType();
		while (((m.equals(Material.LAVA) || m.equals(Material.WATER) || m.equals(Material.STATIONARY_LAVA) 
			|| m.equals(Material.STATIONARY_WATER) || m.equals(Material.SAND) || m.equals(Material.GRAVEL))) &&
			dest.getY() < 120)
		{
			dest = world.getBlockAt(dest.getX(), dest.getY() + 4, dest.getZ());
			m = dest.getType();
		}

		// Not too high or too low overall
		if (dest.getY() > 120)
		{
			dest = world.getBlockAt(dest.getX(), 120, dest.getZ());
		}
		else if (dest.getY() < 8)
		{
			dest = world.getBlockAt(dest.getX(), 8, dest.getZ());
		}

		// Create the physical portal
		// For now, don't worry about direction

		int x = dest.getX(), y = dest.getY(), z = dest.getZ();
		Logger.getLogger("Minecraft").log(Level.INFO, "Creating portal at "+x+","+y+","+z+"."); 

		// Clear area around portal
		ArrayList<Block> columns = new ArrayList<Block>();
		for (int x2 = x - 4; x2 <= x + 4; ++x2)
		{
			for (int z2 = z - 4; z2 <= z + 4; ++z2)
			{
				double dx = x + 0.5f - x2, dz = z - z2;
				if (dx * dx + dz * dz <= 13)
				{
					columns.add(world.getBlockAt(x2, 0, z2));
				}
			}
		}

		// Clear area around portal
		for (Block col : columns)
		{
			// Stone platform
			world.getBlockAt(col.getX(), y - 1, col.getZ()).setType(Material.STONE);
			for (int yd = 0; yd < 4; ++yd)
			{
				world.getBlockAt(col.getX(), y + yd, col.getZ()).setType(Material.AIR);
			}
		}

		// Build obsidian frame
		for (int xd = -1; xd < 3; ++xd)
		{
			for (int yd = -1; yd < 4; ++yd)
			{
				if (xd == -1 || yd == -1 || xd == 2 || yd == 3)
				{
					world.getBlockAt(x + xd, y + yd, z).setType(Material.OBSIDIAN);
				}
			}
		}

		// Set it alight!
		dest.setType(Material.FIRE);

		return new NetherPortal(dest);
	}
}
