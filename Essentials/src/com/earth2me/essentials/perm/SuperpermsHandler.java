package com.earth2me.essentials.perm;

import java.util.List;
import org.bukkit.entity.Player;


public class SuperpermsHandler implements IPermissionsHandler
{
	@Override
	public String getGroup(final Player base)
	{
		return null;
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		return null;
	}

	@Override
	public boolean canBuild(final Player base, final String group)
	{
		return false;
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		return hasPermission(base, "group." + group);
	}

	@Override
	public boolean hasPermission(final Player base, String node)
	{
		String permCheck = node;
		int index;
		while (true)
		{
			if (base.isPermissionSet(permCheck))
			{
				return base.hasPermission(permCheck);
			}
		
			index = node.lastIndexOf('.');
			if (index < 1)
			{
				return base.hasPermission("*");
			}

			node = node.substring(0, index);
			permCheck = node + ".*";
		}
	}

	@Override
	public String getPrefix(final Player base)
	{
		return null;
	}

	@Override
	public String getSuffix(final Player base)
	{
		return null;
	}
}
