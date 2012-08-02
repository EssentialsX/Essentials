package com.earth2me.essentials;

import com.earth2me.essentials.craftbukkit.FakeWorld;
import com.avaje.ebean.config.ServerConfig;
import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import org.bukkit.World.Environment;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;


public class FakeServer implements Server
{
	private List<Player> players = new ArrayList<Player>();
	private final List<World> worlds = new ArrayList<World>();

	public FakeServer()
	{
		if (Bukkit.getServer() == null)
		{
			Bukkit.setServer(this);
		}
	}

	public String getName()
	{
		return "Essentials Fake Server";
	}

	public String getVersion()
	{
		return "1.0";
	}

	@Override
	public Player[] getOnlinePlayers()
	{
		return players.toArray(new Player[0]);
	}

	public void setOnlinePlayers(List<Player> players)
	{
		this.players = players;
	}

	@Override
	public int getMaxPlayers()
	{
		return 100;
	}

	@Override
	public int getPort()
	{
		return 25565;
	}

	@Override
	public String getIp()
	{
		return "127.0.0.1";
	}

	@Override
	public String getServerName()
	{
		return "Test Server";
	}

	@Override
	public String getServerId()
	{
		return "Test Server";
	}

	@Override
	public int broadcastMessage(String string)
	{
		int i = 0;
		for (Player player : players)
		{
			player.sendMessage(string);
			i++;
		}
		return i;
	}

	@Override
	public String getUpdateFolder()
	{
		return "update";
	}

	@Override
	public File getUpdateFolderFile()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Player getPlayer(String string)
	{
		for (Player player : players)
		{
			if (player.getName().equalsIgnoreCase(string))
			{
				return player;
			}
		}
		return null;
	}

	@Override
	public List<Player> matchPlayer(String string)
	{
		List<Player> matches = new ArrayList<Player>();
		for (Player player : players)
		{
			if (player.getName().substring(0, Math.min(player.getName().length(), string.length())).equalsIgnoreCase(string))
			{
				matches.add(player);
			}
		}
		return matches;
	}

	@Override
	public PluginManager getPluginManager()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public BukkitScheduler getScheduler()
	{
		return new BukkitScheduler()
		{
			@Override
			public int scheduleSyncDelayedTask(Plugin plugin, Runnable r, long l)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public int scheduleSyncDelayedTask(Plugin plugin, Runnable r)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public int scheduleSyncRepeatingTask(Plugin plugin, Runnable r, long l, long l1)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public int scheduleAsyncDelayedTask(Plugin plugin, Runnable r, long l)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public int scheduleAsyncDelayedTask(Plugin plugin, Runnable r)
			{
				r.run();
				return 0;
			}

			@Override
			public int scheduleAsyncRepeatingTask(Plugin plugin, Runnable r, long l, long l1)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> clbl)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void cancelTask(int i)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void cancelTasks(Plugin plugin)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void cancelAllTasks()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public boolean isCurrentlyRunning(int i)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public boolean isQueued(int i)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public List<BukkitWorker> getActiveWorkers()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public List<BukkitTask> getPendingTasks()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};
	}

	@Override
	public ServicesManager getServicesManager()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<World> getWorlds()
	{
		return worlds;
	}

	public World createWorld(String string, Environment e)
	{
		World w = new FakeWorld(string, e);
		worlds.add(w);
		return w;
	}

	public World createWorld(String string, Environment e, long l)
	{
		World w = new FakeWorld(string, e);
		worlds.add(w);
		return w;
	}

	@Override
	public World getWorld(String string)
	{
		for (World world : worlds)
		{
			if (world.getName().equalsIgnoreCase(string))
			{
				return world;
			}
		}
		return null;
	}

	@Override
	public void reload()
	{
	}

	@Override
	public Logger getLogger()
	{
		return Logger.getLogger("Minecraft");
	}

	@Override
	public PluginCommand getPluginCommand(String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void savePlayers()
	{
	}

	@Override
	public boolean dispatchCommand(CommandSender cs, String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void configureDbConfig(ServerConfig sc)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean addRecipe(Recipe recipe)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void addPlayer(Player base1)
	{
		players.add(base1);
	}

	public OfflinePlayer createPlayer(String name, IEssentials ess)
	{
		OfflinePlayer player = new OfflinePlayer(name, ess);
		player.setLocation(new Location(worlds.get(0), 0, 0, 0, 0, 0));
		return player;
	}

	@Override
	public World createWorld(WorldCreator creator)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean unloadWorld(String string, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean unloadWorld(World world, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<String, String[]> getCommandAliases()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getSpawnRadius()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setSpawnRadius(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean getOnlineMode()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public World getWorld(long l)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public World getWorld(UUID uuid)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getViewDistance()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean getAllowNether()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean hasWhitelist()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public MapView getMap(short s)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public MapView createMap(World world)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean getAllowFlight()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setWhitelist(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Set<org.bukkit.OfflinePlayer> getWhitelistedPlayers()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void reloadWhitelist()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Player getPlayerExact(String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void shutdown()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int broadcast(String string, String string1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public org.bukkit.OfflinePlayer getOfflinePlayer(final String string)
	{
		return new org.bukkit.OfflinePlayer()
		{
			@Override
			public boolean isOnline()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public String getName()
			{
				return string;
			}

			@Override
			public boolean isBanned()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void setBanned(boolean bln)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public boolean isWhitelisted()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void setWhitelisted(boolean bln)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public Player getPlayer()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public boolean isOp()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void setOp(boolean bln)
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public Map<String, Object> serialize()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public long getFirstPlayed()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public long getLastPlayed()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public boolean hasPlayedBefore()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public Location getBedSpawnLocation()
			{
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};
	}

	@Override
	public Set<String> getIPBans()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void banIP(String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void unbanIP(String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Set<org.bukkit.OfflinePlayer> getBannedPlayers()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public GameMode getDefaultGameMode()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setDefaultGameMode(GameMode gamemode)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ConsoleCommandSender getConsoleSender()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Set getOperators()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getBukkitVersion()
	{
		return "Essentials Fake-Server";
	}

	@Override
	public File getWorldContainer()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public OfflinePlayer[] getOfflinePlayers()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean getAllowEnd()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Messenger getMessenger()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void sendPluginMessage(Plugin plugin, String string, byte[] bytes)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Set<String> getListeningPluginChannels()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean useExactLoginLocation()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getTicksPerAnimalSpawns()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getTicksPerMonsterSpawns()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<Recipe> getRecipesFor(ItemStack is)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Iterator<Recipe> recipeIterator()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void clearRecipes()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void resetRecipes()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public HelpMap getHelpMap()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Inventory createInventory(InventoryHolder ih, InventoryType it)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Inventory createInventory(InventoryHolder ih, int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Inventory createInventory(InventoryHolder ih, int i, String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getWorldType()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean getGenerateStructures()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getConnectionThrottle()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getMonsterSpawnLimit()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getAnimalSpawnLimit()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getWaterAnimalSpawnLimit()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isPrimaryThread()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getMotd()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
