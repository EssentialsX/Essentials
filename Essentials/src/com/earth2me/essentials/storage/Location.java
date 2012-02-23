package com.earth2me.essentials.storage;

import java.lang.ref.WeakReference;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;


public class Location
{
	private WeakReference<org.bukkit.Location> location;
	private final String worldname;
	private UUID worldUID = null;
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;

	public Location(org.bukkit.Location loc)
	{
		location = new WeakReference<org.bukkit.Location>(loc);
		worldname = loc.getWorld().getName();
		worldUID = loc.getWorld().getUID();
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		yaw = loc.getYaw();
		pitch = loc.getPitch();
	}

	public Location(String worldname, double x, double y, double z, float yaw, float pitch)
	{
		this.worldname = worldname;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public Location(String worldname, double x, double y, double z)
	{
		this.worldname = worldname;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = 0f;
		this.pitch = 0f;
	}

	public org.bukkit.Location getBukkitLocation() throws WorldNotLoadedException
	{

		org.bukkit.Location loc = location == null ? null : location.get();
		if (loc == null)
		{
			World world = null;
			if (worldUID != null)
			{
				world = Bukkit.getWorld(worldUID);
			}
			if (world == null)
			{
				world = Bukkit.getWorld(worldname);
			}
			if (world == null)
			{
				throw new WorldNotLoadedException(worldname);
			}
			loc = new org.bukkit.Location(world, getX(), getY(), getZ(), getYaw(), getPitch());
			location = new WeakReference<org.bukkit.Location>(loc);
		}
		return loc;
	}

	public String getWorldName()
	{
		return worldname;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getZ()
	{
		return z;
	}

	public float getYaw()
	{
		return yaw;
	}

	public float getPitch()
	{
		return pitch;
	}


	public static class WorldNotLoadedException extends Exception
	{
		public WorldNotLoadedException(String worldname)
		{
			super("World " + worldname + " is not loaded.");
		}
	}
}
