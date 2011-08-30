package com.earth2me.essentials.perm;

import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;


public class NullPermissionsHandler implements IPermissionsHandler
{
	@Override
	public String getGroup(final Player base)
	{
		return null;
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		return Collections.emptyList();
	}

	@Override
	public boolean canBuild(final Player base, final String group)
	{
		return false;
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		return false;
	}

	@Override
	public boolean hasPermission(final Player base, final String node)
	{
		return false;
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
