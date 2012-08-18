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
	public boolean hasPermission(final Player base, final String node)
	{
		if (base.hasPermission("-" + node))
		{
			return false;
		}
		final String[] parts = node.split("\\.");
		final StringBuilder builder = new StringBuilder(node.length());
		for (String part : parts)
		{
			builder.append('*');
			if (base.hasPermission(builder.toString()))
			{
				return true;
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.append(part).append('.');
		}
		return base.hasPermission(node);
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
