package com.earth2me.essentials.perm;

import java.util.List;

import net.crystalyx.bukkit.simplyperms.SimplyAPI;
import net.crystalyx.bukkit.simplyperms.SimplyPlugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SimplyPermsHandler extends SuperpermsHandler {

	private final transient SimplyAPI api;

	public SimplyPermsHandler(final Plugin plugin) {
		this.api = ((SimplyPlugin) plugin).getAPI();
	}

	@Override
	public String getGroup(final Player base)
	{
		final List<String> groups = api.getPlayerGroups(base.getName());
		if (groups == null || groups.isEmpty()) return null;
		return groups.get(0);
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		return api.getPlayerGroups(base.getName());
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		final List<String> groups = api.getPlayerGroups(base.getName());
		for (String group1 : groups)
		{
			if (group1.equalsIgnoreCase(group))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canBuild(Player base, String group)
	{
		return hasPermission(base, "essentials.build") || hasPermission(base, "permissions.allow.build");
	}

}
