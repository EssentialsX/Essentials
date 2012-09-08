package com.earth2me.essentials.perm;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionInfo;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class PermissionsBukkitHandler extends SuperpermsHandler
{
	private final transient PermissionsPlugin plugin;

	public PermissionsBukkitHandler(final Plugin plugin)
	{
		this.plugin = (PermissionsPlugin)plugin;
	}

	@Override
	public String getGroup(final Player base)
	{
		final List<Group> groups = getPBGroups(base);
		if (groups == null || groups.isEmpty())
		{
			return null;
		}
		return groups.get(0).getName();
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		final List<Group> groups = getPBGroups(base);
		if (groups.size() == 1)
		{
			return Collections.singletonList(groups.get(0).getName());
		}
		final List<String> groupNames = new ArrayList<String>(groups.size());
		for (Group group : groups)
		{
			groupNames.add(group.getName());
		}
		return groupNames;
	}

	private List<Group> getPBGroups(final Player base)
	{
		final PermissionInfo info = plugin.getPlayerInfo(base.getName());
		if (info == null)
		{
			return Collections.emptyList();
		}
		final List<Group> groups = info.getGroups();
		if (groups == null || groups.isEmpty())
		{
			return Collections.emptyList();
		}
		return groups;
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		final List<Group> groups = getPBGroups(base);
		for (Group group1 : groups)
		{
			if (group1.getName().equalsIgnoreCase(group))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canBuild(Player base, String group)
	{
		return false;
	}
}
