package com.earth2me.essentials.api;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;


public interface IPermission
{
	String getPermission();

	Permission getBukkitPermission();
	
	PermissionDefault getPermissionDefault();
}
