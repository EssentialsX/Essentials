package com.earth2me.essentials.user;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Util;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.io.File;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class UserMap extends CacheLoader<String, User> implements IConf
{
	private final transient IEssentials ess;
	private final transient Cache<String, User> users = CacheBuilder.newBuilder().softValues().build(this);
	private final transient ConcurrentSkipListSet<String> keys = new ConcurrentSkipListSet<String>();

	public UserMap(final IEssentials ess)
	{
		super();
		this.ess = ess;
		loadAllUsersAsync(ess);
	}

	private void loadAllUsersAsync(final IEssentials ess)
	{
		ess.scheduleAsyncDelayedTask(new Runnable()
		{
			@Override
			public void run()
			{
				final File userdir = new File(ess.getDataFolder(), "userdata");
				if (!userdir.exists())
				{
					return;
				}
				keys.clear();
				users.invalidateAll();
				for (String string : userdir.list())
				{
					if (!string.endsWith(".yml"))
					{
						continue;
					}
					final String name = string.substring(0, string.length() - 4);
					keys.add(name.toLowerCase(Locale.ENGLISH));
				}
			}
		});
	}

	public boolean userExists(final String name)
	{
		return keys.contains(name.toLowerCase(Locale.ENGLISH));
	}

	public User getUser(final String name)
	{
		try
		{
			return users.get(name.toLowerCase(Locale.ENGLISH));
		}
		catch (ExecutionException ex)
		{
			return null;
		}
		catch (UncheckedExecutionException ex)
		{
			return null;
		}
	}

	@Override
	public User load(final String name) throws Exception
	{
		for (Player player : ess.getServer().getOnlinePlayers())
		{
			if (player.getName().equalsIgnoreCase(name))
			{
				keys.add(name.toLowerCase(Locale.ENGLISH));
				return new User(player, ess);
			}
		}
		final File userFile = getUserFile(name);
		if (userFile.exists())
		{
			keys.add(name.toLowerCase(Locale.ENGLISH));
			return new User(Bukkit.getOfflinePlayer(name), ess);
		}
		throw new Exception("User not found!");
	}

	@Override
	public void reloadConfig()
	{
		loadAllUsersAsync(ess);
	}

	public void removeUser(final String name)
	{
		keys.remove(name.toLowerCase(Locale.ENGLISH));
		users.invalidate(name.toLowerCase(Locale.ENGLISH));
	}

	public Set<String> getAllUniqueUsers()
	{
		return Collections.unmodifiableSet(keys);
	}

	public int getUniqueUsers()
	{
		return keys.size();
	}

	public File getUserFile(final String name)
	{
		final File userFolder = new File(ess.getDataFolder(), "userdata");
		return new File(userFolder, Util.sanitizeFileName(name) + ".yml");
	}
}
