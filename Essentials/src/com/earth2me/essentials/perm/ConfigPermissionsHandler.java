package com.earth2me.essentials.perm;

import com.earth2me.essentials.IEssentials;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class ConfigPermissionsHandler implements IPermissionsHandler
{
	private final transient IEssentials ess;

	public ConfigPermissionsHandler(final Plugin ess)
	{
		this.ess = (IEssentials)ess;
	}

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
		return null;
	}

	@Override
	public String getSuffix(final Player base)
	{
		return null;
	}
}
