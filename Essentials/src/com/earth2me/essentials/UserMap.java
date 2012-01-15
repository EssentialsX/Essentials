package com.earth2me.essentials;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.Player;


public class UserMap implements IConf
{
	private final transient IEssentials ess;
	private final transient Map<String, SoftReference<User>> users = new HashMap<String, SoftReference<User>>();
	//CacheBuilder.newBuilder().softValues().build(this);
	//private final transient ConcurrentSkipListSet<String> keys = new ConcurrentSkipListSet<String>();

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
				synchronized (users)
				{
					users.clear();

					for (String string : userdir.list())
					{
						if (!string.endsWith(".yml"))
						{
							continue;
						}
						final String name = string.substring(0, string.length() - 4);
						users.put(Util.sanitizeFileName(name), null);
					}
				}
			}
		});
	}

	public boolean userExists(final String name)
	{
		return users.containsKey(Util.sanitizeFileName(name));
	}

	public User getUser(final String name)
	{
		try
		{
			synchronized (users)
			{
				final SoftReference<User> softRef = users.get(Util.sanitizeFileName(name));
				User user = softRef == null ? null : softRef.get();
				if (user == null)
				{
					user = load(name);
					users.put(name, new SoftReference<User>(user));
				}
				return user;
			}
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	public User load(final String name) throws Exception
	{
		for (Player player : ess.getServer().getOnlinePlayers())
		{
			if (player.getName().equalsIgnoreCase(name))
			{
				return new User(player, ess);
			}
		}
		final File userFile = getUserFile(name);
		if (userFile.exists())
		{
			return new User(new OfflinePlayer(name, ess), ess);
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
		synchronized (users)
		{
			users.remove(Util.sanitizeFileName(name));
		}
	}

	public Set<String> getAllUniqueUsers()
	{
		synchronized (users)
		{
			return new HashSet<String>(users.keySet());
		}
	}

	public int getUniqueUsers()
	{
		synchronized (users)
		{
			return users.size();
		}
	}

	public File getUserFile(final String name)
	{
		final File userFolder = new File(ess.getDataFolder(), "userdata");
		return new File(userFolder, Util.sanitizeFileName(name) + ".yml");
	}
}
