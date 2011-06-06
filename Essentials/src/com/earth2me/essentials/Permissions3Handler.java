package com.earth2me.essentials;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class Permissions3Handler implements IPermissionsHandler
{
	private final transient PermissionHandler permissionHandler;
	
	Permissions3Handler(final Plugin permissionsPlugin)
	{
		permissionHandler = ((Permissions)permissionsPlugin).getHandler();
	}

	public String getGroup(final Player base)
	{
		return permissionHandler.getPrimaryGroup(base.getWorld().getName(), base.getName());
	}

	public boolean canBuild(final Player base, final String group)
	{
		return permissionHandler.canUserBuild(base.getWorld().getName(), base.getName());
	}

	public boolean inGroup(final Player base, final String group)
	{
		return permissionHandler.inGroup(base.getWorld().getName(), base.getName(), group);
	}

	public boolean hasPermission(final Player base, final String node)
	{
		return permissionHandler.has(base, node);
	}

	public String getPrefix(final Player base)
	{
		return permissionHandler.getUserPrefix(base.getWorld().getName(), base.getName());
	}

	public String getSuffix(final Player base)
	{
		return permissionHandler.getUserSuffix(base.getWorld().getName(), base.getName());
	}
	
}
