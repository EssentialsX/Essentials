package com.earth2me.essentials;

import org.bukkit.entity.Player;


public class BukkitPermissionsHandler implements IPermissionsHandler
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
