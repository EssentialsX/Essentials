package com.earth2me.essentials;

import com.earth2me.essentials.utils.StringUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;


public class UserMap extends CacheLoader<String, User> implements IConf
{
	private final transient IEssentials ess;
	private final transient Cache<String, User> users;
	private final transient ConcurrentSkipListSet<String> keys = new ConcurrentSkipListSet<String>();

	public UserMap(final IEssentials ess)
	{
		super();
		this.ess = ess;
		users = CacheBuilder.newBuilder().maximumSize(ess.getSettings().getMaxUserCacheCount()).softValues().build(this);		
		loadAllUsersAsync(ess);
	}

	private void loadAllUsersAsync(final IEssentials ess)
	{
		ess.runTaskAsynchronously(new Runnable()
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
					keys.add(StringUtil.sanitizeFileName(name));
				}
			}
		});
	}

	public boolean userExists(final String name)
	{
		return keys.contains(StringUtil.sanitizeFileName(name));
	}

	public User getUser(final String name)
	{
		try
		{
			String sanitizedName = StringUtil.sanitizeFileName(name);
			return users.get(sanitizedName);
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
	public User load(final String sanitizedName) throws Exception
	{
		for (Player player : ess.getServer().getOnlinePlayers())
		{			
			String sanitizedPlayer = StringUtil.sanitizeFileName(player.getName());
			if (sanitizedPlayer.equalsIgnoreCase(sanitizedName))
			{
				keys.add(sanitizedName);
				return new User(player, ess);
			}
		}
		final File userFile = getUserFile2(sanitizedName);
		if (userFile.exists())
		{
			keys.add(sanitizedName);
			return new User(new OfflinePlayer(sanitizedName, ess), ess);
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
		keys.remove(StringUtil.sanitizeFileName(name));
		users.invalidate(StringUtil.sanitizeFileName(name));
		users.invalidate(name);
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
		return getUserFile2(StringUtil.sanitizeFileName(name));
	}

	private File getUserFile2(final String name)
	{
		final File userFolder = new File(ess.getDataFolder(), "userdata");
		return new File(userFolder, name + ".yml");
	}
}
