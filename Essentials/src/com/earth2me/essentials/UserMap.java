package com.earth2me.essentials;

import com.earth2me.essentials.utils.StringUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.io.Files;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class UserMap extends CacheLoader<UUID, User> implements IConf
{
	private final transient IEssentials ess;
	private final transient Cache<UUID, User> users;
	private final transient ConcurrentSkipListSet<UUID> keys = new ConcurrentSkipListSet<UUID>();
	private final transient ConcurrentSkipListMap<String, UUID> names = new ConcurrentSkipListMap<String, UUID>();
	private final transient Pattern splitPattern = Pattern.compile(",");
	private File userList;

	public UserMap(final IEssentials ess)
	{
		super();
		this.ess = ess;
		userList = new File(ess.getDataFolder(), "usermap.csv");
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
				names.clear();
				users.invalidateAll();
				for (String string : userdir.list())
				{
					if (!string.endsWith(".yml"))
					{
						continue;
					}
					final String name = string.substring(0, string.length() - 4);
					try
					{
						keys.add(UUID.fromString(name));
					}
					catch (IllegalArgumentException ex)
					{
						//Ignore these users till they rejoin.
					}
				}

				try
				{
					if (!userList.exists())
					{
						userList.createNewFile();
					}

					final BufferedReader reader = new BufferedReader(new FileReader(userList));
					try
					{
						do
						{
							final String line = reader.readLine();
							if (line == null)
							{
								break;
							}
							else
							{
								String[] values = splitPattern.split(line);
								if (values.length == 2)
								{
									names.put(values[0], UUID.fromString(values[1]));
								}
							}
						}
						while (true);
					}
					finally
					{
						reader.close();
					}
				}
				catch (IOException ex)
				{
					Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
				}

			}
		});
	}

	public boolean userExists(final UUID uuid)
	{
		return keys.contains(uuid);
	}

	public User getUser(final String name)
	{
		try
		{
			final String sanitizedName = StringUtil.sanitizeFileName(name);
			if (names.containsKey(sanitizedName))
			{
				final UUID uuid = names.get(sanitizedName);
				return users.get(uuid);
			}

			for (Player player : ess.getServer().getOnlinePlayers())
			{
				String sanitizedPlayer = StringUtil.sanitizeFileName(player.getName());
				if (sanitizedPlayer.equalsIgnoreCase(sanitizedName))
				{
					User user = new User(player, ess);
					trackUUID(user.getBase().getUniqueId(), user.getName());
					return new User(player, ess);
				}
			}

			final File userFile = getUserFileFromString(sanitizedName);
			if (userFile.exists())
			{
				User user = new User(new OfflinePlayer(sanitizedName, ess.getServer()), ess);
				trackUUID(user.getBase().getUniqueId(), user.getName());
				return user;
			}
			return null;
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

	public User getUser(final UUID uuid)
	{
		try
		{
			return users.get(uuid);
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

	public void trackUUID(final UUID uuid, final String name)
	{
		if (uuid != null)
		{
			names.put(StringUtil.sanitizeFileName(name), uuid);
			keys.add(uuid);
			writeUUIDMap();
		}
	}

	public void writeUUIDMap()
	{
		try
		{
			final File tempFile = File.createTempFile("usermap", ".tmp.yml", ess.getDataFolder());
			final BufferedWriter bWriter = new BufferedWriter(new FileWriter(tempFile));

			for (Map.Entry<String, UUID> entry : names.entrySet())
			{
				bWriter.write(entry.getKey() + "," + entry.getValue().toString());
				bWriter.newLine();
			}

			bWriter.close();
			Files.move(tempFile, userList);
		}
		catch (IOException ex)
		{
			Logger.getLogger(UserMap.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public User load(final UUID uuid) throws Exception
	{
		Player player = ess.getServer().getPlayer(uuid);
		if (player != null)
		{
			return new User(player, ess);
		}

		final File userFile = getUserFileFromID(uuid);

		if (userFile.exists())
		{
			keys.add(uuid);
			return new User(new OfflinePlayer(uuid, ess.getServer()), ess);
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
		UUID uuid = names.get(name);
		if (uuid != null)
		{
			keys.remove(uuid);
			users.invalidate(uuid);
		}
		names.remove(name);
		names.remove(StringUtil.sanitizeFileName(name));
	}

	public Set<UUID> getAllUniqueUsers()
	{
		return Collections.unmodifiableSet(keys);
	}

	public int getUniqueUsers()
	{
		return keys.size();
	}

	private File getUserFileFromID(final UUID uuid)
	{
		final File userFolder = new File(ess.getDataFolder(), "userdata");
		return new File(userFolder, uuid.toString() + ".yml");
	}

	public File getUserFileFromString(final String name)
	{
		final File userFolder = new File(ess.getDataFolder(), "userdata");
		return new File(userFolder, StringUtil.sanitizeFileName(name) + ".yml");
	}
}
