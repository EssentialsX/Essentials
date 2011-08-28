package com.earth2me.essentials.perm;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionInfo;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class PermissionsBukkitHandler implements IPermissionsHandler
{
	private final transient PermissionsPlugin plugin;

	public PermissionsBukkitHandler(Plugin plugin)
	{
		this.plugin = (PermissionsPlugin)plugin;
	}
	
	
	public String getGroup(Player base)
	{
		final PermissionInfo info = plugin.getPlayerInfo(base.getName());
		if (info == null) {
			return "default";
		}
		final List<Group> groups = info.getGroups();
		if (groups == null || groups.isEmpty()) {
			return "default";
		}
		return groups.get(0).getName();
	}

	public boolean canBuild(Player base, String group)
	{
		return true;
	}

	public boolean inGroup(Player base, String group)
	{
		final PermissionInfo info = plugin.getPlayerInfo(base.getName());
		if (info == null) {
			return false;
		}
		final List<Group> groups = info.getGroups();
		if (groups == null || groups.isEmpty()) {
			return false;
		}
		for (Group group1 : groups)
		{
			if(group1.getName().equalsIgnoreCase(group)) {
				return true;
			}
		}
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
