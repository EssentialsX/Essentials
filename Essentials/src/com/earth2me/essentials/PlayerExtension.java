package com.earth2me.essentials;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.*;


public class PlayerExtension extends PlayerWrapper
{
	public PlayerExtension(Player base)
	{
		super(base);
	}

	public boolean isBanned()
	{
		return Essentials.getStatic().bans.contains(getName());
	}

	public boolean isIpBanned()
	{
		return Essentials.getStatic().bannedIps.contains(getAddress().getAddress().toString().replace("/", ""));
	}

	public float getCorrectedYaw()
	{
		float angle = (getLocation().getYaw() - 90.0f) % 360.0f;
		if (angle < 0) {
			angle += 360.0f;
		}
		return angle;
	}
	
	public void showInventory(IInventory inventory)
	{
		getHandle().a(inventory);
	}

	public void showInventory(CraftInventoryPlayer inventory)
	{
		showInventory((IInventory)inventory.getInventory());
	}

	public Location getSafeDestination(Location loc) throws Exception
	{
		World world = loc.getWorld();
		double x = Math.floor(loc.getX())+0.5;
		double y = Math.floor(loc.getY());
		double z = Math.floor(loc.getZ())+0.5;

		while (isBlockAboveAir(world, x, y, z))
		{
			y -= 1.0D;
			if (y < 0.0D) {
				throw new Exception("Hole in floor");
			}
		}

		while (isBlockUnsafe(world, x, y, z))
		{
			y += 1.0D;
			if (y >= 110.0D) {
				x += 1.0D;
				break;
			}
		}
		while (isBlockUnsafe(world, x, y, z))
		{
			y -= 1.0D;
			if (y <= 1.0D)
			{
				y = 110.0D;
				x += 1.0D;
			}
		}
		return new Location(world, x, y, z, loc.getYaw(), loc.getPitch());
	}

	private boolean isBlockAboveAir(World world, double x, double y, double z)
	{
		return world.getBlockAt((int)Math.floor(x), (int)Math.floor(y - 1.0D), (int)Math.floor(z)).getType() == Material.AIR;
	}

	public boolean isBlockUnsafe(World world, double x, double y, double z)
	{
		Block below = world.getBlockAt((int)Math.floor(x), (int)Math.floor(y - 1.0D), (int)Math.floor(z));
		if (below.getType() == Material.LAVA || below.getType() == Material.STATIONARY_LAVA)
			return true;

		if (below.getType() == Material.FIRE)
			return true;

		if ((world.getBlockAt((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z)).getType() != Material.AIR)
			|| (world.getBlockAt((int)Math.floor(x), (int)Math.floor(y + 1.0D), (int)Math.floor(z)).getType() != Material.AIR))
		{
			return true;
		}
		return isBlockAboveAir(world, x, y, z);
	}

	public TargetBlock getTarget()
	{
		return new TargetBlock(getBase());
	}

	public String getGroup()
	{
		try
		{
			return com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(getWorld().getName(), getName());
		}
		catch (Throwable ex)
		{
			return "default";
		}
	}

	public boolean canBuild()
	{
		try
		{
			return com.nijikokun.bukkit.Permissions.Permissions.Security.canGroupBuild(getWorld().getName(), getGroup());
		}
		catch (Throwable ex)
		{
			return true;
		}
	}

	public EntityPlayer getHandle()
	{
		return getCraftPlayer().getHandle();
	}

	public CraftPlayer getCraftPlayer()
	{
		return (CraftPlayer)base;
	}
}
