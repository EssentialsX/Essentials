package com.earth2me.essentials.perm;

import org.bukkit.entity.Player;


public class SuperpermsHandler implements IPermissionsHandler
{
	@Override
	public String getGroup(final Player base)
	{
		return "default";
	}

	@Override
	public boolean canBuild(final Player base, final String group)
	{
		return hasPermission(base, "essentials.build");
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		return false;
	}

	@Override
	public boolean hasPermission(final Player base, final String node)
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
	public String getPrefix(final Player base)
	{
		return "";
	}

	@Override
	public String getSuffix(final Player base)
	{
		return "";
	}
}
