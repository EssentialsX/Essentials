package com.earth2me.essentials;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class Permissions2Handler implements IPermissionsHandler
{
	private final transient PermissionHandler permissionHandler;
	
	Permissions2Handler(final Plugin permissionsPlugin)
	{
		permissionHandler = ((Permissions)permissionsPlugin).getHandler();
	}

	public String getGroup(final Player base)
	{
		return permissionHandler.getGroup(base.getWorld().getName(), base.getName());
	}

	public boolean canBuild(final Player base, final String group)
	{
		return permissionHandler.canGroupBuild(base.getWorld().getName(), getGroup(base));
	}

	public boolean inGroup(final Player base, final String group)
	{
		return permissionHandler.inGroup(base.getWorld().getName(), base.getName(), group);
	}

	public boolean hasPermission(final Player base, final String node)
	{
		return permissionHandler.permission(base, node);
	}

	public String getPrefix(final Player base)
	{
		return permissionHandler.getGroupPrefix(base.getWorld().getName(), getGroup(base));
	}

	public String getSuffix(final Player base)
	{
		return permissionHandler.getGroupSuffix(base.getWorld().getName(), getGroup(base));
	}
	
}
