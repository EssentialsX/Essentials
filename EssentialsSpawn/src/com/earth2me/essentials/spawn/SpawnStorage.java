package com.earth2me.essentials.spawn;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.settings.Spawns;
import com.earth2me.essentials.storage.AbstractDelayedYamlFileReader;
import com.earth2me.essentials.storage.AbstractDelayedYamlFileWriter;
import com.earth2me.essentials.storage.StorageObject;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.bukkit.Location;
import org.bukkit.World;


public class SpawnStorage implements IConf, IEssentialsModule
{
	private transient Spawns spawns;
	private final transient IEssentials ess;
	private final transient File spawnfile;
	private final transient ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	public SpawnStorage(final IEssentials ess)
	{
		this.ess = ess;
		spawnfile = new File(ess.getDataFolder(), "spawn.yml");
		new SpawnReader();
	}

	public void setSpawn(final Location loc, final String group)
	{
		rwl.writeLock().lock();
		try
		{
			if (spawns.getSpawns() == null)
			{
				spawns.setSpawns(new HashMap<String, Location>());
			}
			spawns.getSpawns().put(group.toLowerCase(Locale.ENGLISH), loc);
		}
		finally
		{
			rwl.writeLock().unlock();
		}
		new SpawnWriter();

		if ("default".equalsIgnoreCase(group))
		{
			loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		}
	}

	public Location getSpawn(final String group)
	{
		rwl.readLock().lock();
		try
		{
			if (spawns == null || spawns.getSpawns() == null || group == null)
			{
				return getWorldSpawn();
			}
			final Map<String, Location> spawnMap = spawns.getSpawns();
			String groupName = group.toLowerCase(Locale.ENGLISH);
			if (!spawnMap.containsKey(groupName))
			{
				groupName = "default";
			}
			if (!spawnMap.containsKey(groupName))
			{
				return getWorldSpawn();
			}
			return spawnMap.get(groupName);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	private Location getWorldSpawn()
	{
		for (World world : ess.getServer().getWorlds())
		{
			if (world.getEnvironment() != World.Environment.NORMAL)
			{
				continue;
			}
			return world.getSpawnLocation();
		}
		return ess.getServer().getWorlds().get(0).getSpawnLocation();
	}

	@Override
	public void reloadConfig()
	{
		new SpawnReader();
	}


	private class SpawnWriter extends AbstractDelayedYamlFileWriter
	{
		public SpawnWriter()
		{
			super(ess, spawnfile);
		}

		@Override
		public StorageObject getObject()
		{
			rwl.readLock().lock();
			return spawns;
		}

		@Override
		public void onFinish()
		{
			rwl.readLock().unlock();
		}
	}


	private class SpawnReader extends AbstractDelayedYamlFileReader<Spawns>
	{
		public SpawnReader()
		{
			super(ess, spawnfile, Spawns.class);
		}

		@Override
		public void onStart()
		{
			rwl.writeLock().lock();
		}

		@Override
		public void onFinish(final Spawns object)
		{
			spawns = object;
			rwl.writeLock().unlock();
		}
	}
}
