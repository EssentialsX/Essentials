package com.earth2me.essentials.perm;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.worlds.WorldPermissionsManager;
import java.util.List;
import org.bukkit.entity.Player;


public class BPermissionsHandler extends SuperpermsHandler
{
	private final transient WorldPermissionsManager wpm;

	public BPermissionsHandler()
	{
		wpm = Permissions.getWorldPermissionsManager();
	}

	@Override
	public String getGroup(final Player base)
	{
		final List<String> groups = getGroups(base);
		if (groups == null || groups.isEmpty())
		{
			return null;
		}
		return groups.get(0);
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		final PermissionSet pset = wpm.getPermissionSet(base.getWorld());
		if (pset == null)
		{
			return null;
		}
		return pset.getGroups(base);
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		final List<String> groups = getGroups(base);
		if (groups == null || groups.isEmpty())
		{
			return false;
		}
		return groups.contains(group);
	}
}
