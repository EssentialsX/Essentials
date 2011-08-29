package com.earth2me.essentials.perm;

import com.earth2me.essentials.IEssentials;
import org.bukkit.entity.Player;


public class ConfigPermissionsHandler implements IPermissionsHandler
{
	private final transient IEssentials ess;

	public ConfigPermissionsHandler(final IEssentials ess)
	{
		this.ess = ess;
	}

	@Override
	public String getGroup(final Player base)
	{
		return "default";
	}

	@Override
	public boolean canBuild(final Player base, final String group)
	{
		return true;
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		return false;
	}

	@Override
	public boolean hasPermission(final Player base, final String node)
	{
		final String[] cmds = node.split("\\.", 2);
		return !ess.getSettings().isCommandRestricted(cmds[cmds.length - 1])
			   && ess.getSettings().isPlayerCommand(cmds[cmds.length - 1]);
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
