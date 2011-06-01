package com.earth2me.essentials;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;

public class Spawn implements IConf {

	private static final Logger logger = Logger.getLogger("Minecraft");
	private final EssentialsConf config;
	private final Server server;

	public Spawn(Server server, File dataFolder) {
		File configFile = new File(dataFolder, "spawn.yml");
		this.server = server;
		config = new EssentialsConf(configFile);
		config.load();
	}

	public void setSpawn(Location loc, String group) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("world", loc.getWorld().getName());
		map.put("x", loc.getX());
		map.put("y", loc.getY());
		map.put("z", loc.getZ());
		map.put("yaw", loc.getYaw());
		map.put("pitch", loc.getPitch());
		config.setProperty(group, map);
		config.save();

		if ("default".equals(group)) {
			loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		}
	}

	public Location getSpawn(String group) {
		if (config.getProperty(group) == null) {
			group = "default";
		}
		if (config.getProperty(group) == null) {
			for (World w : server.getWorlds()) {
				if (w.getEnvironment() != Environment.NORMAL) {
					continue;
				}
				return w.getSpawnLocation();
			}
		}
		String worldId = config.getString(group + ".world", "");
		World world = server.getWorlds().get(server.getWorlds().size() > 1 ? 1 : 0);
		for (World w : server.getWorlds()) {
			if (w.getEnvironment() != Environment.NORMAL) {
				continue;
			}
			world = w;
			break;
		}
		for (World w : server.getWorlds()) {
			if (!w.getName().equals(worldId)) {
				continue;
			}
			world = w;
			break;
		}

		double x = config.getDouble(group + ".x", config.getDouble("default.x", 0));
		double y = config.getDouble(group + ".y", config.getDouble("default.y", 0));
		double z = config.getDouble(group + ".z", config.getDouble("default.z", 0));
		float yaw = (float) config.getDouble(group + ".yaw", config.getDouble("default.yaw", 0));
		float pitch = (float) config.getDouble(group + ".pitch", config.getDouble("default.pitch", 0));
		Location retval = new Location(world, x, y, z, yaw, pitch);

		if (y < 1) {
			retval.setY(world.getHighestBlockYAt(retval));
		}

		return retval;
	}

	public void reloadConfig() {
		config.load();
	}
}
