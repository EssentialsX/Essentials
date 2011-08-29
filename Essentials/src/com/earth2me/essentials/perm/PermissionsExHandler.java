package com.earth2me.essentials.perm;

import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;


public class PermissionsExHandler implements IPermissionsHandler
{
	private final transient PermissionManager manager;

	public PermissionsExHandler()
	{
		manager = PermissionsEx.getPermissionManager();
	}

	@Override
	public String getGroup(final Player base)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return "default";
		}
		return user.getGroupsNames()[0];
	}

	@Override
	public boolean canBuild(final Player base, final String group)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return true;
		}

		return user.getOptionBoolean("build", base.getWorld().getName(), true);
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return false;
		}

		return user.inGroup(group);
	}

	@Override
	public boolean hasPermission(final Player base, final String node)
	{
		return manager.has(base.getName(), node, base.getWorld().getName());
	}

	@Override
	public String getPrefix(final Player base)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return "";
		}
		return user.getPrefix();
	}

	@Override
	public String getSuffix(final Player base)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return "";
		}
		return user.getSuffix();
	}
}
