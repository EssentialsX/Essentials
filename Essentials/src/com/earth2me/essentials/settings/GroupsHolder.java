package com.earth2me.essentials.settings;

import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IGroups;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;


public class GroupsHolder extends AsyncStorageObjectHolder<Groups> implements IGroups
{
	public GroupsHolder(final IEssentials ess)
	{
		super(ess, Groups.class);
	}

	@Override
	public File getStorageFile()
	{
		return new File(ess.getDataFolder(), "groups.yml");
	}

	public void registerPermissions()
	{
		acquireReadLock();
		try
		{
			final Map<String, GroupOptions> groups = getData().getGroups();
			if (groups == null || groups.isEmpty())
			{
				return;
			}
			Util.registerPermissions("essentials.groups", groups.keySet(), true, ess);
		}
		finally
		{
			unlock();
		}
	}

	public Collection<GroupOptions> getGroups(final IUser player)
	{
		acquireReadLock();
		try
		{
			final Map<String, GroupOptions> groups = getData().getGroups();
			if (groups == null || groups.isEmpty())
			{
				return Collections.emptyList();
			}
			final ArrayList<GroupOptions> list = new ArrayList();
			for (Entry<String, GroupOptions> entry : groups.entrySet())
			{
				if (player.isAuthorized("essentials.groups." + entry.getKey()))
				{
					list.add(entry.getValue());
				}
			}
			return list;
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public double getHealCooldown(final IUser player)
	{
		for (GroupOptions groupOptions : getGroups(player))
		{
			if (groupOptions.getHealCooldown() != null)
			{
				return groupOptions.getHealCooldown();
			}
		}
		return 0;
	}

	@Override
	public double getTeleportCooldown(final IUser player)
	{
		for (GroupOptions groupOptions : getGroups(player))
		{
			if (groupOptions.getTeleportCooldown() != null)
			{
				return groupOptions.getTeleportCooldown();
			}
		}
		return 0;
	}
	
	@Override
	public double getTeleportDelay(final IUser player)
	{
		for (GroupOptions groupOptions : getGroups(player))
		{
			if (groupOptions.getTeleportDelay() != null)
			{
				return groupOptions.getTeleportDelay();
			}
		}
		return 0;
	}

	@Override
	public String getPrefix(final IUser player)
	{
		for (GroupOptions groupOptions : getGroups(player))
		{
			if (groupOptions.getPrefix() != null)
			{
				return groupOptions.getPrefix();
			}
		}
		return "";
	}

	@Override
	public String getSuffix(final IUser player)
	{
		for (GroupOptions groupOptions : getGroups(player))
		{
			if (groupOptions.getSuffix() != null)
			{
				return groupOptions.getSuffix();
			}
		}
		return "";
	}
}
