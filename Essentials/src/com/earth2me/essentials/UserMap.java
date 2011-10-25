package com.earth2me.essentials;

import com.google.common.base.Function;
import com.google.common.collect.ComputationException;
import com.google.common.collect.MapMaker;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class UserMap implements Function<String, User>, IConf
{
	private final transient IEssentials ess;
	private final transient ConcurrentMap<String, User> users = new MapMaker().softValues().makeComputingMap(this);

	public UserMap(final IEssentials ess)
	{
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
				for (String string : userdir.list())
				{
					if (!string.endsWith(".yml"))
					{
						continue;
					}
					final String name = string.substring(0, string.length() - 4);
					try
					{
						users.get(name.toLowerCase());
					}
					catch (NullPointerException ex)
					{
						// Ignore these
					}
					catch (ComputationException ex)
					{
						Bukkit.getLogger().log(Level.INFO, "Failed to preload user " + name, ex);
					}
				}
			}
		});
	}

	public boolean userExists(final String name)
	{
		return users.containsKey(name.toLowerCase());
	}

	public User getUser(final String name) throws NullPointerException
	{
		return users.get(name.toLowerCase());
	}

	@Override
	public User apply(final String name)
	{
		for (Player player : ess.getServer().getOnlinePlayers())
		{
			if (player.getName().equalsIgnoreCase(name))
			{
				return new User(player, ess);
			}
		}
		final File userFolder = new File(ess.getDataFolder(), "userdata");
		final File userFile = new File(userFolder, Util.sanitizeFileName(name) + ".yml");
		if (userFile.exists())
		{
			return new User(new OfflinePlayer(name, ess), ess);
		}
		return null;
	}

	@Override
	public void reloadConfig()
	{
		for (User user : users.values())
		{
			user.reloadConfig();
		}
	}

	public void removeUser(final String name)
	{
		users.remove(name.toLowerCase());
	}

	public Set<User> getAllUsers()
	{
		final Set<User> userSet = new HashSet<User>();
		for (String name : users.keySet())
		{
			userSet.add(users.get(name));
		}
		return userSet;
	}

	public int getUniqueUsers()
	{
		return users.size();
	}
}
