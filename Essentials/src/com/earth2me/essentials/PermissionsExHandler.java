package com.earth2me.essentials;

import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;


class PermissionsExHandler implements IPermissionsHandler
{
	private final transient PermissionManager manager;

	public PermissionsExHandler()
	{
		manager = PermissionsEx.getPermissionManager();
	}

	public String getGroup(Player base)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return "default";
		}
		return user.getGroupsNames()[0];
	}

	public boolean canBuild(Player base, String group)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return true;
		}

		return user.getOptionBoolean("build", base.getWorld().getName(), true);
	}

	public boolean inGroup(Player base, String group)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return false;
		}

		return user.inGroup(group);
	}

	public boolean hasPermission(Player base, String node)
	{
		return manager.has(base.getName(), node, base.getWorld().getName());
	}

	public String getPrefix(Player base)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return "";
		}
		return user.getPrefix();
	}

	public String getSuffix(Player base)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return "";
		}
		return user.getSuffix();
	}
}
