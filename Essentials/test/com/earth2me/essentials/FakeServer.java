package com.earth2me.essentials;

import com.avaje.ebean.config.ServerConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.scheduler.BukkitScheduler;


public class FakeServer implements Server
{
	private List<Player> players = new ArrayList<Player>();
	private final List<World> worlds = new ArrayList<World>();

	public String getName()
	{
		return "Test Server";
	}

	public String getVersion()
	{
		return "1.0";
	}

	public Player[] getOnlinePlayers()
	{
		return players.toArray(new Player[0]);
	}
	
	public void setOnlinePlayers(List<Player> players)
	{
		this.players = players;
	}

	public int getMaxPlayers()
	{
		return 100;
	}

	public int getPort()
	{
		return 25565;
	}

	public String getIp()
	{
		return "127.0.0.1";
	}

	public String getServerName()
	{
		return "Test Server";
	}

	public String getServerId()
	{
		return "Test Server";
	}

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

	public String getUpdateFolder()
	{
		return "update";
	}

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

	public PluginManager getPluginManager()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public BukkitScheduler getScheduler()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public ServicesManager getServicesManager()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

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

	public World getWorld(String string)
	{
		for (World world : worlds)
		{
			if (world.getName().equalsIgnoreCase(string)) {
				return world;
			}
		}
		return null;
	}

	public void reload()
	{
	}

	public Logger getLogger()
	{
		return Logger.getLogger("Minecraft");
	}

	public PluginCommand getPluginCommand(String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void savePlayers()
	{
	}

	public boolean dispatchCommand(CommandSender cs, String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void configureDbConfig(ServerConfig sc)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean addRecipe(Recipe recipe)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void addPlayer(Player base1)
	{
		players.add(base1);
	}
	
	public OfflinePlayer createPlayer(String name)
	{
		OfflinePlayer player = new OfflinePlayer(name);
		player.setLocation(new Location(worlds.get(0), 0, 0, 0, 0, 0));
		return player;
	}

	public World createWorld(String string, Environment e, ChunkGenerator cg)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public World createWorld(String string, Environment e, long l, ChunkGenerator cg)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean unloadWorld(String string, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean unloadWorld(World world, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
