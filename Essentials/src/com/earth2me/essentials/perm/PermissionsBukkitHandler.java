package com.earth2me.essentials.perm;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionInfo;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
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
		final PermissionInfo info = plugin.getPlayerInfo(base.getName());
		if (info == null)
		{
			return "default";
		}
		final List<Group> groups = info.getGroups();
		if (groups == null || groups.isEmpty())
		{
			return "default";
		}
		return groups.get(0).getName();
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		final PermissionInfo info = plugin.getPlayerInfo(base.getName());
		if (info == null)
		{
			return false;
		}
		final List<Group> groups = info.getGroups();
		if (groups == null || groups.isEmpty())
		{
			return false;
		}
		for (Group group1 : groups)
		{
			if (group1.getName().equalsIgnoreCase(group))
			{
				return true;
			}
		}
		return false;
	}
}
