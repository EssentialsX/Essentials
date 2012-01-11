package com.earth2me.essentials.craftbukkit;

import java.lang.ref.WeakReference;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;


public class BetterLocation extends Location
{
	private transient String worldName;
	private static BetterLocationListener listener = new BetterLocationListener();

	public static BetterLocationListener getListener()
	{
		return listener;
	}

	public static void cleanup()
	{
		synchronized (listener.locationMap)
		{
			listener.locationMap.clear();
		}
	}

	public BetterLocation(final String worldName, final double x, final double y, final double z)
	{
		super(Bukkit.getWorld(worldName), x, y, z);
		this.worldName = worldName;
		addToMap(this);
	}

	public BetterLocation(final String worldName, final double x, final double y,
						  final double z, final float yaw, final float pitch)
	{
		super(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
		this.worldName = worldName;
		addToMap(this);
	}

	public BetterLocation(final World world, final double x, final double y, final double z)
	{
		super(world, x, y, z);
		if (world == null)
		{
			throw new WorldNotLoadedException();
		}
		this.worldName = world.getName();
		addToMap(this);
	}

	public BetterLocation(final World world, final double x, final double y,
						  final double z, final float yaw, final float pitch)
	{
		super(world, x, y, z, yaw, pitch);
		if (world == null)
		{
			throw new WorldNotLoadedException();
		}
		this.worldName = world.getName();
		addToMap(this);
	}

	public BetterLocation(final Location location)
	{
		super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		if (location.getWorld() == null)
		{
			throw new WorldNotLoadedException();
		}
		this.worldName = location.getWorld().getName();
		addToMap(this);
	}

	@Override
	public World getWorld()
	{
		World world = super.getWorld();
		if (world == null)
		{
			world = Bukkit.getWorld(worldName);
		}
		if (world == null)
		{
			throw new WorldNotLoadedException();
		}
		else
		{
			super.setWorld(world);
		}
		return world;
	}

	@Override
	public void setWorld(final World world)
	{
		if (world == null)
		{
			throw new WorldNotLoadedException();
		}
		if (!world.getName().equals(this.worldName))
		{
			getListener().removeLocation(this);
			this.worldName = world.getName();
			addToMap(this);
		}
		super.setWorld(world);
	}

	public String getWorldName()
	{
		return worldName;
	}

	private void addToMap(final BetterLocation location)
	{
		synchronized (listener.locationMap)
		{
			List<WeakReference<Location>> locations = listener.locationMap.get(location.getWorldName());
			if (locations == null)
			{
				locations = new LinkedList<WeakReference<Location>>();
				listener.locationMap.put(location.getWorldName(), locations);
			}
			locations.add(new WeakReference<Location>(location));
		}
	}


	public static class WorldNotLoadedException extends RuntimeException
	{
		public WorldNotLoadedException()
		{
			super("World is not loaded.");
		}
	}


	public static class BetterLocationListener extends org.bukkit.event.world.WorldListener implements Runnable
	{
		private static Random random = new Random();
		private final transient Map<String, List<WeakReference<Location>>> locationMap = new HashMap<String, List<WeakReference<Location>>>();

		@Override
		public void onWorldLoad(final WorldLoadEvent event)
		{
			final String worldName = event.getWorld().getName();
			synchronized (locationMap)
			{
				final List<WeakReference<Location>> locations = locationMap.get(worldName);
				if (locations != null)
				{
					for (final Iterator<WeakReference<Location>> it = locations.iterator(); it.hasNext();)
					{
						final WeakReference<Location> weakReference = it.next();
						final Location loc = weakReference.get();
						if (loc == null)
						{
							it.remove();
						}
						else
						{
							loc.setWorld(event.getWorld());
						}
					}
				}
			}
		}

		@Override
		public void onWorldUnload(final WorldUnloadEvent event)
		{
			final String worldName = event.getWorld().getName();
			synchronized (locationMap)
			{
				final List<WeakReference<Location>> locations = locationMap.get(worldName);
				if (locations != null)
				{
					for (final Iterator<WeakReference<Location>> it = locations.iterator(); it.hasNext();)
					{
						final WeakReference<Location> weakReference = it.next();
						final Location loc = weakReference.get();
						if (loc == null)
						{
							it.remove();
						}
						else
						{
							loc.setWorld(null);
						}
					}
				}
			}
		}

		@Override
		public void run()
		{
			synchronized (locationMap)
			{
				// Pick a world by random
				final Collection<List<WeakReference<Location>>> allWorlds = locationMap.values();
				final int randomPick = (allWorlds.isEmpty() ? 0 : random.nextInt(allWorlds.size()));
				List<WeakReference<Location>> locations = null;
				final Iterator<List<WeakReference<Location>>> iterator = allWorlds.iterator();
				for (int i = 0; iterator.hasNext() && i < randomPick; i++)
				{
					iterator.next();
				}
				if (iterator.hasNext())
				{
					locations = iterator.next();
				}
				if (locations != null)
				{
					for (final Iterator<WeakReference<Location>> it = locations.iterator(); it.hasNext();)
					{
						final WeakReference<Location> weakReference = it.next();
						final Location loc = weakReference.get();
						if (loc == null)
						{
							it.remove();
						}
					}
				}
			}
		}

		private void removeLocation(final BetterLocation location)
		{
			final String worldName = location.getWorldName();
			synchronized (locationMap)
			{
				final List<WeakReference<Location>> locations = locationMap.get(worldName);
				if (locations != null)
				{
					for (final Iterator<WeakReference<Location>> it = locations.iterator(); it.hasNext();)
					{
						final WeakReference<Location> weakReference = it.next();
						final Location loc = weakReference.get();
						if (loc == null || loc == location)
						{
							it.remove();
						}
					}
				}
			}
		}
	}
}
