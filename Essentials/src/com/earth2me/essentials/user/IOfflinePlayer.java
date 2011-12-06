package com.earth2me.essentials.user;

import org.bukkit.Location;


public interface IOfflinePlayer
{
	String getName();

	String getDisplayName();

	Location getBedSpawnLocation();
	
	void setBanned(boolean bln);
}