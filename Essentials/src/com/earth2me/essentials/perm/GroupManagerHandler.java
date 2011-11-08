package com.earth2me.essentials.perm;

import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;


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
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		return handler.getGroup(base.getName());
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		return Arrays.asList(handler.getGroups(base.getName()));
	}

	@Override
	public boolean canBuild(final Player base, final String group)
	{
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		return handler.canUserBuild(base.getName());
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		return handler.inGroup(base.getName(), group);
	}

	@Override
	public boolean hasPermission(final Player base, final String node)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		return handler.has(base, node);
	}

	@Override
	public String getPrefix(final Player base)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		return handler.getUserPrefix(base.getName());
	}

	@Override
	public String getSuffix(final Player base)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		return handler.getUserSuffix(base.getName());
	}
}
