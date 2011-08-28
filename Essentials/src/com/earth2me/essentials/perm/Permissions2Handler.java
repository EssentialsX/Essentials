package com.earth2me.essentials.perm;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class Permissions2Handler implements IPermissionsHandler
{
	private final transient PermissionHandler permissionHandler;

	public Permissions2Handler(final Plugin permissionsPlugin)
	{
		permissionHandler = ((Permissions)permissionsPlugin).getHandler();
	}

	public String getGroup(final Player base)
	{
		final String group = permissionHandler.getGroup(base.getWorld().getName(), base.getName());
		return group == null ? "default" : group;
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
		final String prefix = permissionHandler.getGroupPrefix(base.getWorld().getName(), getGroup(base));
		return prefix == null ? "" : prefix;
	}

	public String getSuffix(final Player base)
	{
		final String suffix = permissionHandler.getGroupSuffix(base.getWorld().getName(), getGroup(base));
		return suffix == null ? "" : suffix;
	}
}
