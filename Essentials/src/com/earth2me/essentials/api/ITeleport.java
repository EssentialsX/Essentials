package com.earth2me.essentials.api;

import org.bukkit.Location;


public interface ITeleport
{
	void now(Location loc, boolean cooldown) throws Exception;
}
