package com.earth2me.essentials;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerListener;

public class Jail extends BlockListener implements IConf {
	private static final Logger logger = Logger.getLogger("Minecraft");
	private EssentialsConf config;

	public Jail(File dataFolder) {
		config = new EssentialsConf(new File(dataFolder, "jail.yml"));
		config.load();
	}

	public void setJail(Location loc, String jailName) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("world", loc.getWorld().getName());
		map.put("x", loc.getX());
		map.put("y", loc.getY());
		map.put("z", loc.getZ());
		map.put("yaw", loc.getYaw());
		map.put("pitch", loc.getPitch());
		config.setProperty(jailName.toLowerCase(), map);
		config.save();
	}

	public Location getJail(String jailName) throws Exception {
		if (config.getProperty(jailName.toLowerCase()) == null) {
			throw new Exception("That jail does not exist");
		}

		World jWorld = null;
		String world = config.getString(jailName + ".world", ""); // wh.spawnX
		double x = config.getDouble(jailName + ".x", 0); // wh.spawnX
		double y = config.getDouble(jailName + ".y", 0); // wh.spawnY
		double z = config.getDouble(jailName + ".z", 0); // wh.spawnZ
		float yaw = (float) config.getDouble(jailName + ".yaw", 0);
		float pitch = (float) config.getDouble(jailName + ".pitch", 0);
		for (World w : Essentials.getStatic().getServer().getWorlds()) {
			if (w.getName().equalsIgnoreCase(world)) {
				jWorld = w;
				break;
			}

		}
		return new Location(jWorld, x, y, z, yaw, pitch);
	}

	public void sendToJail(User user, String jail) throws Exception {
		user.teleportTo(getJail(jail));
		user.currentJail = jail;
	}

	public void delJail(String jail) throws Exception {
		config.removeProperty(jail.toLowerCase());
		config.save();
	}

	public List<String> getJails() throws Exception {
		return config.getKeys(null);
	}

	public void reloadConfig() {
		config.load();
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event)
	{
		User user = User.get(event.getPlayer());
		if (user.isJailed()) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		User user = User.get(event.getPlayer());
		if (user.isJailed()) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		User user = User.get(event.getPlayer());
		if (user.isJailed()) {
			event.setCancelled(true);
		}
	}
}
	

