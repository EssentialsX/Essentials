package com.earth2me.essentials.user;

import org.bukkit.Location;
import org.bukkit.permissions.Permission;


public interface IOfflinePlayer
{
	String getName();

	String getDisplayName();

	Location getBedSpawnLocation();
	
	void setBanned(boolean bln);
	
	boolean hasPermission(Permission perm);
}