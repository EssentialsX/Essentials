package com.earth2me.essentials;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class Warps implements IConf {

	private static final Logger logger = Logger.getLogger("Minecraft");
	Map<StringIgnoreCase, EssentialsConf> warpPoints = new HashMap<StringIgnoreCase, EssentialsConf>();
	File warpsFolder;
	Server server;

	public Warps(Server server, File dataFolder) {
		this.server = server;
		warpsFolder = new File(dataFolder, "warps");
		if (!warpsFolder.exists()) {
			warpsFolder.mkdirs();
		} else {
			convertWarps(dataFolder);
		}
		reloadConfig();
	}

	private String convertToFileName(String name) {
		return name.toLowerCase().replaceAll("[^a-z0-9]", "_");
	}

	public boolean isEmpty() {
		return warpPoints.isEmpty();
	}

	public Iterable<String> getWarpNames() {
		List<String> keys = new ArrayList<String>();
		for (StringIgnoreCase stringIgnoreCase : warpPoints.keySet()) {
			keys.add(stringIgnoreCase.string);
		}
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
		return keys;
	}

	public Location getWarp(String warp) throws Exception {
		EssentialsConf conf = warpPoints.get(new StringIgnoreCase(warp));
		if (conf == null) {
			throw new Exception("That warp does not exist.");
		}
		double x = conf.getDouble("x", 0);
		double y = conf.getDouble("y", 0);
		double z = conf.getDouble("z", 0);
		float yaw = (float) conf.getDouble("yaw", 0);
		float pitch = (float) conf.getDouble("pitch", 0);
		String world = conf.getString("world");
		World w = server.getWorld(world);
		if (w == null) {
			throw new Exception("World of warp does not exist.");
		}
		return new Location(w, x, y, z, yaw, pitch);
	}

	public void setWarp(String name, Location loc) throws Exception {
		setWarp(name, loc, null);
	}

	private void setWarp(String name, Location loc, String worldName) throws Exception {
		String filename = convertToFileName(name);
		EssentialsConf conf = warpPoints.get(new StringIgnoreCase(name));
		if (conf == null) {
			File confFile = new File(warpsFolder, filename + ".yml");
			if (confFile.exists()) {
				throw new Exception("A warp with a similar name already exists.");
			}
			conf = new EssentialsConf(confFile);
			conf.setProperty("name", name);
			warpPoints.put(new StringIgnoreCase(name), conf);
		}
		conf.setProperty("x", loc.getBlockX());
		conf.setProperty("y", loc.getBlockY());
		conf.setProperty("z", loc.getBlockZ());
		conf.setProperty("yaw", loc.getYaw());
		conf.setProperty("pitch", loc.getPitch());
		if (worldName != null) {
			conf.setProperty("world", worldName);
		} else {
			conf.setProperty("world", loc.getWorld().getName());
		}
		conf.save();
	}

	public void delWarp(String name) throws Exception {
		EssentialsConf conf = warpPoints.get(new StringIgnoreCase(name));
		if (conf == null) {
			throw new Exception("Warp does not exist.");
		}
		if (!conf.getFile().delete()) {
			throw new Exception("Problem deleting the warp file.");
		}
		warpPoints.remove(new StringIgnoreCase(name));
	}

	private void convertWarps(File dataFolder) {
		File[] listOfFiles = warpsFolder.listFiles();
		if (listOfFiles.length >= 1) {
			for (int i = 0; i < listOfFiles.length; i++) {
				String filename = listOfFiles[i].getName();
				if (listOfFiles[i].isFile() && filename.endsWith(".dat")) {
					try {
						BufferedReader rx = new BufferedReader(new FileReader(listOfFiles[i]));
						double x = Double.parseDouble(rx.readLine().trim());
						double y = Double.parseDouble(rx.readLine().trim());
						double z = Double.parseDouble(rx.readLine().trim());
						float yaw = Float.parseFloat(rx.readLine().trim());
						float pitch = Float.parseFloat(rx.readLine().trim());
						String worldName = rx.readLine();
						rx.close();
						World w = null;
						for (World world : server.getWorlds()) {
							if (world.getEnvironment() != World.Environment.NETHER) {
								w = world;
								break;
							}
						}
						boolean forceWorldName = false;
						if (worldName != null) {
							worldName.trim();
							World w1 = null;
							for (World world : server.getWorlds()) {
								if (world.getName().equalsIgnoreCase(worldName)) {
									w1 = world;
									break;
								}
							}
							if (w1 != null) {
								w = w1;
							} else {
								File worldFolder = new File(dataFolder.getAbsoluteFile().getParentFile().getParentFile(), worldName);
								if (worldFolder.exists() && worldFolder.isDirectory()) {
									logger.log(Level.WARNING, "World " + worldName + " not loaded, but directory found. Converting warp anyway.");
									forceWorldName = true;
								}
							}
						}
						Location loc = new Location(w, x, y, z, yaw, pitch);
						setWarp(filename.substring(0, filename.length() - 4), loc, forceWorldName ? worldName : null);
						if(!listOfFiles[i].renameTo(new File(warpsFolder, filename + ".old")))
						{
							throw new Exception("Renaming file " + filename + " failed");
						}
					} catch (Exception ex) {
						logger.log(Level.SEVERE, null, ex);
					}
				}
			}

		}
		File warpFile = new File(dataFolder, "warps.txt");
		if (warpFile.exists()) {
			try {
				BufferedReader rx = new BufferedReader(new FileReader(warpFile));
				for (String[] parts = new String[0]; rx.ready(); parts = rx.readLine().split(":")) {
					if (parts.length < 6) {
						continue;
					}
					String name = parts[0];
					double x = Double.parseDouble(parts[1].trim());
					double y = Double.parseDouble(parts[2].trim());
					double z = Double.parseDouble(parts[3].trim());
					float yaw = Float.parseFloat(parts[4].trim());
					float pitch = Float.parseFloat(parts[5].trim());
					if (name.isEmpty()) {
						continue;
					}
					World w = null;
					for (World world : server.getWorlds()) {
						if (world.getEnvironment() != World.Environment.NETHER) {
							w = world;
							break;
						}
					}
					Location loc = new Location(w, x, y, z, yaw, pitch);
					setWarp(name, loc);
					if(!warpFile.renameTo(new File(dataFolder, "warps.txt.old")));
					{
							throw new Exception("Renaming warps.txt failed");
					}
				}
			} catch (Exception ex) {
				logger.log(Level.SEVERE, null, ex);
			}
		}
	}

	public final void reloadConfig() {
		warpPoints.clear();
		File[] listOfFiles = warpsFolder.listFiles();
		if (listOfFiles.length >= 1) {
			for (int i = 0; i < listOfFiles.length; i++) {
				String filename = listOfFiles[i].getName();
				if (listOfFiles[i].isFile() && filename.endsWith(".yml")) {
					EssentialsConf conf = new EssentialsConf(listOfFiles[i]);
					conf.load();
					String name = conf.getString("name");
					if (name != null) {
						warpPoints.put(new StringIgnoreCase(name), conf);
					}
				}
			}
		}
	}

	private class StringIgnoreCase {

		String string;

		public StringIgnoreCase(String string) {
			this.string = string;
		}

		@Override
		public int hashCode() {
			return string.toLowerCase().hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof String) {
				return string.equalsIgnoreCase((String) o);
			}
			if (o instanceof StringIgnoreCase) {
				return string.equalsIgnoreCase(((StringIgnoreCase) o).string);
			}
			return false;
		}
	}
}
