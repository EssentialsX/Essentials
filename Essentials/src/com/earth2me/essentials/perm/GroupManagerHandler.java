package com.earth2me.essentials.perm;

import java.util.Arrays;
import java.util.List;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class GroupManagerHandler implements IPermissionsHandler
{
	private final transient GroupManager groupManager;

	public GroupManagerHandler(final Plugin permissionsPlugin)
	{
		groupManager = ((GroupManager)permissionsPlugin);
	}

	@Override
	public String getGroup(final Player base)
	{
		final AnjoPermissionsHandler handler = getHandler(base);
		if (handler == null)
		{
			return null;
		}
		return handler.getGroup(base.getName());
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		final AnjoPermissionsHandler handler = getHandler(base);
		if (handler == null)
		{
			return null;
		}
		return Arrays.asList(handler.getGroups(base.getName()));
	}

	@Override
	public boolean canBuild(final Player base, final String group)
	{
		final AnjoPermissionsHandler handler = getHandler(base);
		if (handler == null)
		{
			return false;
		}
		return handler.canUserBuild(base.getName());
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		AnjoPermissionsHandler handler = getHandler(base);
		if (handler == null)
		{
			return false;
		}
		return handler.inGroup(base.getName(), group);
	}

	@Override
	public boolean hasPermission(final Player base, final String node)
	{
		AnjoPermissionsHandler handler = getHandler(base);
		if (handler == null)
		{
			return false;
		}
		return handler.has(base, node);
	}

	@Override
	public String getPrefix(final Player base)
	{
		AnjoPermissionsHandler handler = getHandler(base);
		if (handler == null)
		{
			return null;
		}
		return handler.getUserPrefix(base.getName());
	}

	@Override
	public String getSuffix(final Player base)
	{
		AnjoPermissionsHandler handler = getHandler(base);
		if (handler == null)
		{
			return null;
		}
		return handler.getUserSuffix(base.getName());
	}

	private AnjoPermissionsHandler getHandler(final Player base)
	{
		final WorldsHolder holder = groupManager.getWorldsHolder();
		if (holder == null)
		{
			return null;
		}
		return holder.getWorldPermissions(base);
	}
}
