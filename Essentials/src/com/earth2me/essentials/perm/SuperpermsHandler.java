package com.earth2me.essentials.perm;

import org.bukkit.entity.Player;


public class SuperpermsHandler implements IPermissionsHandler
{
	public String getGroup(Player base)
	{
		return "default";
	}

	public boolean canBuild(Player base, String group)
	{
		return true;
	}

	public boolean inGroup(Player base, String group)
	{
		return false;
	}

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

	public String getPrefix(Player base)
	{
		return "";
	}

	public String getSuffix(Player base)
	{
		return "";
	}
}

