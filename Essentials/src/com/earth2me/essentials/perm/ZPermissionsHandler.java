package com.earth2me.essentials.perm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.plugin.Plugin;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;


public class ZPermissionsHandler extends SuperpermsHandler implements Listener
{
	private ZPermissionsService service = null;
	private boolean hasGetPlayerPrimaryGroup = false; // This is a post-1.0 addition

	public ZPermissionsHandler(Plugin plugin)
	{
		acquireZPermissionsService();
		if (!isReady())
		{
			// Shouldn't get to this point, since caller checks if zPerms
			// is enabled. But for the sake of correctness...
			Bukkit.getPluginManager().registerEvents(this, plugin);
		}
	}

	@EventHandler
	public void onServiceRegister(ServiceRegisterEvent event)
	{
		if (ZPermissionsService.class.equals(event.getProvider().getService()))
		{
			acquireZPermissionsService();
		}
	}

	@Override
	public String getGroup(Player base)
	{
		if (!isReady())
		{
			return super.getGroup(base);
		}
		else
		{
			return getPrimaryGroup(base.getName());
		}
	}

	@Override
	public List<String> getGroups(Player base)
	{
		if (!isReady())
		{
			return super.getGroups(base);
		}
		else
		{
			return new ArrayList<String>(service.getPlayerGroups(base.getName()));
		}
	}

	@Override
	public boolean inGroup(Player base, String group)
	{
		if (!isReady())
		{
			return super.inGroup(base, group);
		}
		else
		{
			Set<String> groups = service.getPlayerGroups(base.getName());
			for (String test : groups)
			{
				if (test.equalsIgnoreCase(group))
				{
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public String getPrefix(Player base)
	{
		if (!isReady())
		{
			return super.getPrefix(base);
		}
		else
		{
			return getPrefixSuffix(base, "prefix");
		}
	}

	@Override
	public String getSuffix(Player base)
	{
		if (!isReady())
		{
			return super.getSuffix(base);
		}
		else
		{
			return getPrefixSuffix(base, "suffix");
		}
	}

	private String getPrefixSuffix(Player base, String metadataName)
	{
		String playerPrefixSuffix;
		try
		{
			playerPrefixSuffix = service.getPlayerMetadata(base.getName(), metadataName, String.class);
		}
		catch (IllegalStateException e)
		{
			// User error. They set prefix to a non-string.
			playerPrefixSuffix = null;
		}
		if (playerPrefixSuffix == null)
		{
			// Try prefix/suffix of their "primary group"
			try
			{
				return service.getGroupMetadata(getPrimaryGroup(base.getName()), metadataName, String.class);
			}
			catch (IllegalStateException e)
			{
				// User error, again
				return null;
			}
		}
		else
		{
			return playerPrefixSuffix;
		}
	}

	private void acquireZPermissionsService()
	{
		service = Bukkit.getServicesManager().load(ZPermissionsService.class);
		if (isReady())
		{
			// getPlayerPrimaryGroup(String) was added in an unreleased version
			// Check if it exists.
			try
			{
				service.getClass().getMethod("getPlayerPrimaryGroup", String.class);
				hasGetPlayerPrimaryGroup = true;
			}
			catch (NoSuchMethodException e)
			{
				hasGetPlayerPrimaryGroup = false;
			}
			catch (SecurityException e)
			{
				hasGetPlayerPrimaryGroup = false;
			}
		}
	}

	private boolean isReady()
	{
		return service != null;
	}

	private String getPrimaryGroup(String playerName)
	{
		if (hasGetPlayerPrimaryGroup)
		{
			return service.getPlayerPrimaryGroup(playerName);
		}
		else
		{
			// Fall back to using highest-weight assigned group
			List<String> groups = service.getPlayerAssignedGroups(playerName);
			return groups.get(0);
		}
	}
}
