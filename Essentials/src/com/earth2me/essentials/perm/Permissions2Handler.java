package com.earth2me.essentials.perm;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class Permissions2Handler implements IPermissionsHandler
{
	private final transient PermissionHandler permissionHandler;

	public Permissions2Handler(final Plugin permissionsPlugin)
	{
		permissionHandler = ((Permissions)permissionsPlugin).getHandler();
	}

	@Override
	public String getGroup(final Player base)
	{
		return permissionHandler.getGroup(base.getWorld().getName(), base.getName());
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		return Arrays.asList(permissionHandler.getGroups(base.getWorld().getName(), base.getName()));
	}

	@Override
	public boolean canBuild(final Player base, final String group)
	{
		return permissionHandler.canGroupBuild(base.getWorld().getName(), getGroup(base));
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		return permissionHandler.inGroup(base.getWorld().getName(), base.getName(), group);
	}

	@Override
	public boolean hasPermission(final Player base, final String node)
	{
		return permissionHandler.permission(base.getName(), node);
	}

	@Override
	public String getPrefix(final Player base)
	{
		return permissionHandler.getGroupPrefix(base.getWorld().getName(), getGroup(base));
	}

	@Override
	public String getSuffix(final Player base)
	{
		return permissionHandler.getGroupSuffix(base.getWorld().getName(), getGroup(base));
	}
}
