package com.earth2me.essentials.settings;

import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IGroups;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Cleanup;


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
					if(entry.getValue() != null)
					{
					list.add(entry.getValue());
					}
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

	@Override
	public int getHomeLimit(final IUser player)
	{
		for (GroupOptions groupOptions : getGroups(player))
		{
			if (groupOptions.getHomes() != null)
			{
				return groupOptions.getHomes();
			}
		}
		return 0;
	}
	
	//TODO: Reimplement caching
	@Override
	public MessageFormat getChatFormat(final IUser player)
	{
			String format = getRawChatFormat(player);
			format = Util.replaceColor(format);
			format = format.replace("{DISPLAYNAME}", "%1$s");
			format = format.replace("{GROUP}", "{0}");
			format = format.replace("{MESSAGE}", "%2$s");
			format = format.replace("{WORLDNAME}", "{1}");
			format = format.replace("{SHORTWORLDNAME}", "{2}");
			format = format.replaceAll("\\{(\\D*)\\}", "\\[$1\\]");
			MessageFormat mFormat = new MessageFormat(format);
			return mFormat;		
	}
	
	private String getRawChatFormat(final IUser player)
	{
		for (GroupOptions groupOptions : getGroups(player))
		{
			if (groupOptions != null && groupOptions.getMessageFormat() != null)
			{
				return groupOptions.getMessageFormat();
			}
		}
		@Cleanup
		ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		return settings.getData().getChat().getDefaultFormat();
	}
	
}
