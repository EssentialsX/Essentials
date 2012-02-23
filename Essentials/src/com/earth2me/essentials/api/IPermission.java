package com.earth2me.essentials.api;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;


public interface IPermission
{
	String getPermission();

	boolean isAuthorized(CommandSender sender);

	Permission getBukkitPermission();

	PermissionDefault getPermissionDefault();
}
