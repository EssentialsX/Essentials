package com.earth2me.essentials;

import org.bukkit.entity.Player;


public class ConfigPermissionsHandler implements IPermissionsHandler
{
	private final transient IEssentials ess;

	public ConfigPermissionsHandler(final IEssentials ess)
	{
		this.ess = ess;
	}

	public String getGroup(final Player base)
	{
		return "default";
	}

	public boolean canBuild(final Player base, final String group)
	{
		return true;
	}

	public boolean inGroup(final Player base, final String group)
	{
		return false;
	}

	public boolean hasPermission(final Player base, final String node)
	{
		final String[] cmds = node.split("\\.", 2);
		return !ess.getSettings().isCommandRestricted(cmds[cmds.length - 1]) 
				&& ess.getSettings().isPlayerCommand(cmds[cmds.length - 1]);
	}

	public String getPrefix(final Player base)
	{
		return "";
	}

	public String getSuffix(final Player base)
	{
		return "";
	}
}
