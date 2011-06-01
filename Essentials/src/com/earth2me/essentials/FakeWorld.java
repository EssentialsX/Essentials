package com.earth2me.essentials;

import java.util.List;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class FakeWorld implements World
{

	private final String name;
	private final Environment env;
	FakeWorld(String string, Environment environment)
	{
		this.name = string;
		this.env = environment;
	}

	public Block getBlockAt(int i, int i1, int i2)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Block getBlockAt(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getBlockTypeIdAt(int i, int i1, int i2)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getBlockTypeIdAt(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getHighestBlockYAt(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getHighestBlockYAt(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Chunk getChunkAt(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Chunk getChunkAt(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Chunk getChunkAt(Block block)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isChunkLoaded(Chunk chunk)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Chunk[] getLoadedChunks()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void loadChunk(Chunk chunk)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isChunkLoaded(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void loadChunk(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean loadChunk(int i, int i1, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean unloadChunk(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean unloadChunk(int i, int i1, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean unloadChunk(int i, int i1, boolean bln, boolean bln1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean unloadChunkRequest(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean unloadChunkRequest(int i, int i1, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean regenerateChunk(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean refreshChunk(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Item dropItem(Location lctn, ItemStack is)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Item dropItemNaturally(Location lctn, ItemStack is)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Arrow spawnArrow(Location lctn, Vector vector, float f, float f1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean generateTree(Location lctn, TreeType tt)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean generateTree(Location lctn, TreeType tt, BlockChangeDelegate bcd)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Minecart spawnMinecart(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public StorageMinecart spawnStorageMinecart(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public PoweredMinecart spawnPoweredMinecart(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Boat spawnBoat(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public LivingEntity spawnCreature(Location lctn, CreatureType ct)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public LightningStrike strikeLightning(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public LightningStrike strikeLightningEffect(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<Entity> getEntities()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<LivingEntity> getLivingEntities()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<Player> getPlayers()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getName()
	{
		return name;
	}

	public long getId()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Location getSpawnLocation()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean setSpawnLocation(int i, int i1, int i2)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public long getTime()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setTime(long l)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public long getFullTime()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setFullTime(long l)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean hasStorm()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setStorm(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getWeatherDuration()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setWeatherDuration(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isThundering()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setThundering(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getThunderDuration()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setThunderDuration(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Environment getEnvironment()
	{
		return env;
	}

	public long getSeed()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean getPVP()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setPVP(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void save()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
