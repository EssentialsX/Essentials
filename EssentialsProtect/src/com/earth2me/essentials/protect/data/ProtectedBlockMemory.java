package com.earth2me.essentials.protect.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class ProtectedBlockMemory implements IProtectedBlock {

	List<String> worlds = new ArrayList<String>();
	List<String> playerNames = new ArrayList<String>();
	IProtectedBlock storage;
	Plugin plugin;

	class ProtectedLocation {

		int x;
		int y;
		int z;
		int w;

		private ProtectedLocation(Block block, int worldId) {
			this.x = block.getX();
			this.y = block.getY();
			this.z = block.getZ();
			this.w = worldId;
		}

		private ProtectedLocation(OwnedBlock ownedBlock, int worldId) {
			this.x = ownedBlock.x;
			this.y = ownedBlock.y;
			this.z = ownedBlock.z;
			this.w = worldId;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof ProtectedLocation) {
				ProtectedLocation pl = (ProtectedLocation) o;
				return x == pl.x && y == pl.y && z == pl.z && w == pl.w;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return x ^ y ^ z ^ w;
		}
	}

	class ProtectedBy {

		private int playerId = -1;
		private Set<Integer> playerIds;

		private ProtectedBy() {
		}

		private void add(int playerId) {
			if (this.playerId == -1 || this.playerId == playerId) {
				this.playerId = playerId;
			} else {
				if (playerIds == null) {
					playerIds = new HashSet<Integer>(4);
					playerIds.add(this.playerId);
				}
				playerIds.add(playerId);
			}
		}

		private boolean contains(int playerId) {
			if (playerIds == null) {
				return this.playerId == playerId;
			}
			return playerIds.contains(playerId);
		}

		private List<String> getPlayers(List<String> playerNames) {
			if (playerIds == null) {
				List<String> list = new ArrayList<String>(2);
				list.add(playerNames.get(playerId));
				return list;
			}
			List<String> list = new ArrayList<String>(playerIds.size());
			for (Integer integer : playerIds) {
				list.add(playerNames.get(integer));
			}
			return list;
		}

		private int size() {
			if (playerIds == null) {
				return 1;
			}
			return playerIds.size();
		}
	}
	HashMap<ProtectedLocation, ProtectedBy> blocks = new HashMap<ProtectedLocation, ProtectedBy>();

	public ProtectedBlockMemory(IProtectedBlock storage) {
		this.storage = storage;
		importProtections(storage.exportProtections());
	}

	public void clearProtections() {
		blocks.clear();
	}

	public final void importProtections(List<OwnedBlock> blocks) {
		for (OwnedBlock ownedBlock : blocks) {
			ProtectedLocation pl = new ProtectedLocation(ownedBlock, getWorldId(ownedBlock.world));
			if (ownedBlock.playerName == null) {
				continue;
			}
			protectBlock(pl, ownedBlock.playerName);
		}
	}

	public List<OwnedBlock> exportProtections() {
		List<OwnedBlock> blockList = new ArrayList<OwnedBlock>(blocks.size());
		for (Entry<ProtectedLocation, ProtectedBy> entry : blocks.entrySet()) {
			for (String name : entry.getValue().getPlayers(playerNames)) {
				OwnedBlock ob = new OwnedBlock();
				ob.x = entry.getKey().x;
				ob.y = entry.getKey().y;
				ob.z = entry.getKey().z;
				ob.world = worlds.get(entry.getKey().w);
				ob.playerName = name;
				blockList.add(ob);
			}
		}
		return blockList;
	}

	public void protectBlock(final Block block, final String playerName) {
		ProtectedLocation pl = new ProtectedLocation(block, getWorldId(block.getWorld()));
		protectBlock(pl, playerName);
		plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				storage.protectBlock(block, playerName);
			}
		});
	}

	private void protectBlock(ProtectedLocation pl, String playerName) {
		int playerId = getPlayerId(playerName);
		ProtectedBy pb = blocks.get(pl);
		if (pb == null) {
			pb = new ProtectedBy();
			blocks.put(pl, pb);
		}
		pb.add(playerId);
	}

	public boolean isProtected(Block block, String playerName) {
		int playerId = getPlayerId(playerName);
		ProtectedLocation pl = new ProtectedLocation(block, getWorldId(block.getWorld()));
		ProtectedBy pb = blocks.get(pl);
		return !pb.contains(playerId);
	}

	public List<String> getOwners(Block block) {
		ProtectedLocation pl = new ProtectedLocation(block, getWorldId(block.getWorld()));
		ProtectedBy pb = blocks.get(pl);
		return pb.getPlayers(playerNames);
	}

	public int unprotectBlock(final Block block) {
		ProtectedLocation pl = new ProtectedLocation(block, getWorldId(block.getWorld()));
		ProtectedBy pb = blocks.remove(pl);
		plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				storage.unprotectBlock(block);
			}
		});
		return pb.size();
	}

	private int getPlayerId(String playername) {
		int id = playerNames.indexOf(playername);
		if (id < 0) {
			playerNames.add(playername);
			id = playerNames.indexOf(playername);
		}
		return id;
	}

	private int getWorldId(World world) {
		return getWorldId(world.getName());
	}

	private int getWorldId(String name) {
		int id = worlds.indexOf(name);
		if (id < 0) {
			worlds.add(name);
			id = worlds.indexOf(name);
		}
		return id;
	}
}
