package com.earth2me.essentials.perm;

import java.util.Arrays;
import java.util.List;
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
			return null;
		}
		return user.getGroupsNames()[0];
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return null;
		}
		return Arrays.asList(user.getGroupsNames());
	}

	@Override
	public boolean canBuild(final Player base, final String group)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return false;
		}

		return user.getOptionBoolean("build", base.getWorld().getName(), false);
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
		return base.hasPermission(node);
	}

	@Override
	public String getPrefix(final Player base)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return null;
		}
		return user.getPrefix(base.getWorld().getName());
	}

	@Override
	public String getSuffix(final Player base)
	{
		final PermissionUser user = manager.getUser(base.getName());
		if (user == null)
		{
			return null;
		}

		return user.getSuffix(base.getWorld().getName());
	}
}
