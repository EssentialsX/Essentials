package com.earth2me.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class PlayerTarget implements ITarget
{
	private final Location location;
	private final String name;

	PlayerTarget(Player entity)
	{
		this.name = entity.getName();
		this.location = null;
	}

	@Override
	public Location getLocation()
	{
		if (this.name != null)
		{
			return Bukkit.getServer().getPlayerExact(name).getLocation();
		}
		return location;
	}
}