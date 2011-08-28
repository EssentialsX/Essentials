package com.earth2me.essentials.perm;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.worlds.WorldPermissionsManager;
import java.util.List;
import org.bukkit.entity.Player;


public class BPermissionsHandler implements IPermissionsHandler
{
	private final transient WorldPermissionsManager wpm;

	public BPermissionsHandler()
	{
		wpm = Permissions.getWorldPermissionsManager();
	}

	@Override
	public String getGroup(final Player base)
	{
		final PermissionSet pset = wpm.getPermissionSet(base.getWorld());
		if (pset == null)
		{
			return "default";
		}
		final List<String> groups = pset.getGroups(base);
		if (groups == null || groups.isEmpty())
		{
			return "default";
		}
		return groups.get(0);
	}

	@Override
	public boolean canBuild(Player base, String group)
	{
		return true;
	}

	@Override
	public boolean inGroup(Player base, String group)
	{
		final PermissionSet pset = wpm.getPermissionSet(base.getWorld());
		if (pset == null)
		{
			return false;
		}
		final List<String> groups = pset.getGroups(base);
		if (groups == null || groups.isEmpty())
		{
			return false;
		}
		return groups.contains(group);
	}

	@Override
	public boolean hasPermission(Player base, String node)
	{
		if (base.hasPermission("-" + node))
		{
			return false;
		}
		final String[] parts = node.split("\\.");
		final StringBuilder sb = new StringBuilder();
		for (String part : parts)
		{
			if (base.hasPermission(sb.toString() + "*"))
			{
				return true;
			}
			sb.append(part).append(".");
		}
		return base.hasPermission(node);
	}

	@Override
	public String getPrefix(Player base)
	{
		return "";
	}

	@Override
	public String getSuffix(Player base)
	{
		return "";
	}
}
