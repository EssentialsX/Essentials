package com.earth2me.essentials;

import org.bukkit.Location;
import org.bukkit.Server;


public class LocationTarget implements ITarget
{
	private final Location location;
	private final String name;

	LocationTarget(Location location)
	{
		this.location = location;
		this.name = null;
	}

	@Override
	public Location getLocation(Server server)
	{
		if (this.name != null)
		{

			return server.getPlayerExact(name).getLocation();
		}
		return location;
	}
}