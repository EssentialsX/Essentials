package com.earth2me.essentials.protect.data;

import java.util.*;
import java.util.Map.Entry;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;


public class ProtectedBlockMemory implements IProtectedBlock
{
	private final transient List<String> worlds = new ArrayList<String>();
	private final transient List<String> playerNames = new ArrayList<String>();
	private final transient IProtectedBlock storage;
	private final transient Plugin plugin;


	static class ProtectedLocation
	{
		private final transient int x;
		private final transient int y;
		private final transient int z;
		private final transient int w;

		public ProtectedLocation(final Block block, final int worldId)
		{
			this.x = block.getX();
			this.y = block.getY();
			this.z = block.getZ();
			this.w = worldId;
		}

		public ProtectedLocation(final OwnedBlock ownedBlock, final int worldId)
		{
			this.x = ownedBlock.x;
			this.y = ownedBlock.y;
			this.z = ownedBlock.z;
			this.w = worldId;
		}

		@Override
		public boolean equals(final Object object)
		{
			if (object instanceof ProtectedLocation)
			{
				final ProtectedLocation pLoc = (ProtectedLocation)object;
				return x == pLoc.x && y == pLoc.y && z == pLoc.z && w == pLoc.w;
			}
			return false;
		}

		@Override
		public int hashCode()
		{
			return x ^ y ^ z ^ w;
		}
	}


	static class ProtectedBy
	{
		private transient int playerId = -1;
		private transient Set<Integer> playerIds;

		public void add(final int playerId)
		{
			if (this.playerId == -1 || this.playerId == playerId)
			{
				this.playerId = playerId;
			}
			else
			{
				if (playerIds == null)
				{
					playerIds = new HashSet<Integer>(4);
					playerIds.add(this.playerId);
				}
				playerIds.add(playerId);
			}
		}

		public boolean contains(final int playerId)
		{
			if (playerIds == null)
			{
				return this.playerId == playerId;
			}
			return playerIds.contains(playerId);
		}

		public List<String> getPlayers(final List<String> playerNames)
		{
			final List<String> list = new ArrayList<String>(2);
			if (playerIds == null)
			{
				list.add(playerNames.get(playerId));
			}
			else
			{
				for (Integer integer : playerIds)
				{
					list.add(playerNames.get(integer));
				}
			}
			return list;
		}

		public int size()
		{
			if (playerIds == null)
			{
				return 1;
			}
			return playerIds.size();
		}
	}
	private final transient Map<ProtectedLocation, ProtectedBy> blocks = new HashMap<ProtectedLocation, ProtectedBy>();

	public ProtectedBlockMemory(final IProtectedBlock storage, final Plugin plugin)
	{
		this.storage = storage;
		this.plugin = plugin;
		importProtections(storage.exportProtections());
	}

	@Override
	public void clearProtections()
	{
		blocks.clear();
	}

	@Override
	public final void importProtections(final List<OwnedBlock> blocks)
	{
		for (OwnedBlock ownedBlock : blocks)
		{
			final ProtectedLocation pl = new ProtectedLocation(ownedBlock, getWorldId(ownedBlock.world));
			if (ownedBlock.playerName == null)
			{
				continue;
			}
			protectBlock(pl, ownedBlock.playerName);
		}
	}

	@Override
	public List<OwnedBlock> exportProtections()
	{
		final List<OwnedBlock> blockList = new ArrayList<OwnedBlock>(blocks.size());
		for (Entry<ProtectedLocation, ProtectedBy> entry : blocks.entrySet())
		{
			for (String name : entry.getValue().getPlayers(playerNames))
			{
				final OwnedBlock ob = new OwnedBlock(
						entry.getKey().x,
						entry.getKey().y,
						entry.getKey().z,
						worlds.get(entry.getKey().w),
						name);
				blockList.add(ob);
			}
		}
		return blockList;
	}

	@Override
	public void protectBlock(final Block block, final String playerName)
	{
		final ProtectedLocation pl = new ProtectedLocation(block, getWorldId(block.getWorld()));
		protectBlock(pl, playerName);
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				storage.protectBlock(block, playerName);
			}
		});
	}

	private void protectBlock(ProtectedLocation pl, String playerName)
	{
		int playerId = getPlayerId(playerName);
		ProtectedBy pb = blocks.get(pl);
		if (pb == null)
		{
			pb = new ProtectedBy();
			blocks.put(pl, pb);
		}
		pb.add(playerId);
	}

	@Override
	public boolean isProtected(Block block, String playerName)
	{
		int playerId = getPlayerId(playerName);
		ProtectedLocation pl = new ProtectedLocation(block, getWorldId(block.getWorld()));
		ProtectedBy pb = blocks.get(pl);
		return !pb.contains(playerId);
	}

	@Override
	public List<String> getOwners(Block block)
	{
		ProtectedLocation pl = new ProtectedLocation(block, getWorldId(block.getWorld()));
		ProtectedBy pb = blocks.get(pl);
		return pb.getPlayers(playerNames);
	}

	@Override
	public int unprotectBlock(final Block block)
	{
		ProtectedLocation pl = new ProtectedLocation(block, getWorldId(block.getWorld()));
		ProtectedBy pb = blocks.remove(pl);
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				storage.unprotectBlock(block);
			}
		});
		return pb.size();
	}

	private int getPlayerId(String playername)
	{
		int id = playerNames.indexOf(playername);
		if (id < 0)
		{
			playerNames.add(playername);
			id = playerNames.indexOf(playername);
		}
		return id;
	}

	private int getWorldId(World world)
	{
		return getWorldId(world.getName());
	}

	private int getWorldId(String name)
	{
		int id = worlds.indexOf(name);
		if (id < 0)
		{
			worlds.add(name);
			id = worlds.indexOf(name);
		}
		return id;
	}

	@Override
	public void onPluginDeactivation()
	{
		storage.onPluginDeactivation();
	}
}
