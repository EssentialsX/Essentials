package com.earth2me.essentials;

import com.earth2me.essentials.craftbukkit.FakeWorld;
import com.earth2me.essentials.settings.Spawns;
import com.earth2me.essentials.storage.YamlStorageWriter;
import static com.earth2me.essentials.I18n._;
import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;


public class EssentialsUpgrade
{
	private final static Logger LOGGER = Logger.getLogger("Minecraft");
	private final transient IEssentials ess;
	private final transient EssentialsConf doneFile;

	EssentialsUpgrade(final IEssentials essentials)
	{
		ess = essentials;
		if (!ess.getDataFolder().exists())
		{
			ess.getDataFolder().mkdirs();
		}
		doneFile = new EssentialsConf(new File(ess.getDataFolder(), "upgrades-done.yml"));
		doneFile.load();
	}

	private void moveWorthValuesToWorthYml()
	{
		if (doneFile.getBoolean("moveWorthValuesToWorthYml", false))
		{
			return;
		}
		try
		{
			final File configFile = new File(ess.getDataFolder(), "config.yml");
			if (!configFile.exists())
			{
				return;
			}
			final EssentialsConf conf = new EssentialsConf(configFile);
			conf.load();
			final Worth worth = new Worth(ess.getDataFolder());
			boolean found = false;
			for (Material mat : Material.values())
			{
				final int id = mat.getId();
				final double value = conf.getDouble("worth-" + id, Double.NaN);
				if (!Double.isNaN(value))
				{
					found = true;
					worth.setPrice(new ItemStack(mat, 1, (short)0, (byte)0), value);
				}
			}
			if (found)
			{
				removeLinesFromConfig(configFile, "\\s*#?\\s*worth-[0-9]+.*", "# Worth values have been moved to worth.yml");
			}
			doneFile.setProperty("moveWorthValuesToWorthYml", true);
			doneFile.save();
		}
		catch (Throwable e)
		{
			LOGGER.log(Level.SEVERE, _("upgradingFilesError"), e);
		}
	}

	private void moveMotdRulesToFile(String name)
	{
		if (doneFile.getBoolean("move" + name + "ToFile", false))
		{
			return;
		}
		try
		{
			final File file = new File(ess.getDataFolder(), name + ".txt");
			if (file.exists())
			{
				return;
			}
			final File configFile = new File(ess.getDataFolder(), "config.yml");
			if (!configFile.exists())
			{
				return;
			}
			final EssentialsConf conf = new EssentialsConf(configFile);
			conf.load();
			List<String> lines = conf.getStringList(name);
			if (lines != null && !lines.isEmpty())
			{
				if (!file.createNewFile())
				{
					throw new IOException("Failed to create file " + file);
				}
				PrintWriter writer = new PrintWriter(file);

				for (String line : lines)
				{
					writer.println(line);
				}
				writer.close();
			}
			doneFile.setProperty("move" + name + "ToFile", true);
			doneFile.save();
		}
		catch (Throwable e)
		{
			LOGGER.log(Level.SEVERE, _("upgradingFilesError"), e);
		}
	}

	private void removeLinesFromConfig(File file, String regex, String info) throws Exception
	{
		boolean needUpdate = false;
		final BufferedReader bReader = new BufferedReader(new FileReader(file));
		final File tempFile = File.createTempFile("essentialsupgrade", ".tmp.yml", ess.getDataFolder());
		final BufferedWriter bWriter = new BufferedWriter(new FileWriter(tempFile));
		do
		{
			final String line = bReader.readLine();
			if (line == null)
			{
				break;
			}
			if (line.matches(regex))
			{
				if (!needUpdate && info != null)
				{
					bWriter.write(info, 0, info.length());
					bWriter.newLine();
				}
				needUpdate = true;
			}
			else
			{
				if (line.endsWith("\r\n"))
				{
					bWriter.write(line, 0, line.length() - 2);
				}
				else if (line.endsWith("\r") || line.endsWith("\n"))
				{
					bWriter.write(line, 0, line.length() - 1);
				}
				else
				{
					bWriter.write(line, 0, line.length());
				}
				bWriter.newLine();
			}
		}
		while (true);
		bReader.close();
		bWriter.close();
		if (needUpdate)
		{
			if (!file.renameTo(new File(file.getParentFile(), file.getName().concat("." + System.currentTimeMillis() + ".upgradebackup"))))
			{
				throw new Exception(_("configFileMoveError"));
			}
			if (!tempFile.renameTo(file))
			{
				throw new Exception(_("configFileRenameError"));
			}
		}
		else
		{
			tempFile.delete();
		}
	}

	private void updateUsersToNewDefaultHome()
	{
		if (doneFile.getBoolean("updateUsersToNewDefaultHome", false))
		{
			return;
		}
		final File userdataFolder = new File(ess.getDataFolder(), "userdata");
		if (!userdataFolder.exists() || !userdataFolder.isDirectory())
		{
			return;
		}
		final File[] userFiles = userdataFolder.listFiles();

		for (File file : userFiles)
		{
			if (!file.isFile() || !file.getName().endsWith(".yml"))
			{
				continue;
			}
			final EssentialsConf config = new EssentialsConf(file);
			try
			{
				config.load();
				if (config.hasProperty("home") && !config.hasProperty("home.default"))
				{
					@SuppressWarnings("unchecked")
					final List<Object> vals = (List<Object>)config.getProperty("home");
					if (vals == null)
					{
						continue;
					}
					World world = ess.getServer().getWorlds().get(0);
					if (vals.size() > 5)
					{
						world = ess.getServer().getWorld((String)vals.get(5));
					}
					if (world != null)
					{
						final Location loc = new Location(
								world,
								((Number)vals.get(0)).doubleValue(),
								((Number)vals.get(1)).doubleValue(),
								((Number)vals.get(2)).doubleValue(),
								((Number)vals.get(3)).floatValue(),
								((Number)vals.get(4)).floatValue());

						final String worldName = world.getName().toLowerCase(Locale.ENGLISH);
						if (worldName != null && !worldName.isEmpty())
						{
							config.removeProperty("home");
							config.setProperty("home.default", worldName);
							config.setProperty("home.worlds." + worldName, loc);
							config.save();
						}
					}
				}
			}
			catch (RuntimeException ex)
			{
				LOGGER.log(Level.INFO, "File: " + file.toString());
				throw ex;
			}
		}
		doneFile.setProperty("updateUsersToNewDefaultHome", true);
		doneFile.save();
	}

	private void updateUsersPowerToolsFormat()
	{
		if (doneFile.getBoolean("updateUsersPowerToolsFormat", false))
		{
			return;
		}
		final File userdataFolder = new File(ess.getDataFolder(), "userdata");
		if (!userdataFolder.exists() || !userdataFolder.isDirectory())
		{
			return;
		}
		final File[] userFiles = userdataFolder.listFiles();

		for (File file : userFiles)
		{
			if (!file.isFile() || !file.getName().endsWith(".yml"))
			{
				continue;
			}
			final EssentialsConf config = new EssentialsConf(file);
			try
			{
				config.load();
				if (config.hasProperty("powertools"))
				{
					@SuppressWarnings("unchecked")
					final Map<String, Object> powertools = config.getConfigurationSection("powertools").getValues(false);
					if (powertools == null)
					{
						continue;
					}
					for (Map.Entry<String, Object> entry : powertools.entrySet())
					{
						if (entry.getValue() instanceof String)
						{
							List<String> temp = new ArrayList<String>();
							temp.add((String)entry.getValue());
							((Map<String, Object>)powertools).put(entry.getKey(), temp);
						}
					}
					config.save();
				}
			}
			catch (RuntimeException ex)
			{
				LOGGER.log(Level.INFO, "File: " + file.toString());
				throw ex;
			}
		}
		doneFile.setProperty("updateUsersPowerToolsFormat", true);
		doneFile.save();
	}

	private void updateUsersHomesFormat()
	{
		if (doneFile.getBoolean("updateUsersHomesFormat", false))
		{
			return;
		}
		final File userdataFolder = new File(ess.getDataFolder(), "userdata");
		if (!userdataFolder.exists() || !userdataFolder.isDirectory())
		{
			return;
		}
		final File[] userFiles = userdataFolder.listFiles();

		for (File file : userFiles)
		{
			if (!file.isFile() || !file.getName().endsWith(".yml"))
			{
				continue;
			}
			final EssentialsConf config = new EssentialsConf(file);
			try
			{

				config.load();
				if (config.hasProperty("home") && config.hasProperty("home.default"))
				{
					@SuppressWarnings("unchecked")
					final String defworld = (String)config.getProperty("home.default");
					final Location defloc = getFakeLocation(config, "home.worlds." + defworld);
					if (defloc != null)
					{
						config.setProperty("homes.home", defloc);
					}

					Set<String> worlds = config.getConfigurationSection("home.worlds").getKeys(false);
					Location loc;
					String worldName;

					if (worlds == null)
					{
						continue;
					}
					for (String world : worlds)
					{
						if (defworld.equalsIgnoreCase(world))
						{
							continue;
						}
						loc = getFakeLocation(config, "home.worlds." + world);
						if (loc == null)
						{
							continue;
						}
						worldName = loc.getWorld().getName().toLowerCase(Locale.ENGLISH);
						if (worldName != null && !worldName.isEmpty())
						{
							config.setProperty("homes." + worldName, loc);
						}
					}
					config.removeProperty("home");
					config.save();
				}

			}
			catch (RuntimeException ex)
			{
				LOGGER.log(Level.INFO, "File: " + file.toString());
				throw ex;
			}
		}
		doneFile.setProperty("updateUsersHomesFormat", true);
		doneFile.save();
	}

	private void moveUsersDataToUserdataFolder()
	{
		final File usersFile = new File(ess.getDataFolder(), "users.yml");
		if (!usersFile.exists())
		{
			return;
		}
		final EssentialsConf usersConfig = new EssentialsConf(usersFile);
		usersConfig.load();
		for (String username : usersConfig.getKeys(false))
		{
			final User user = new User(new OfflinePlayer(username, ess), ess);
			final String nickname = usersConfig.getString(username + ".nickname");
			if (nickname != null && !nickname.isEmpty() && !nickname.equals(username))
			{
				user.setNickname(nickname);
			}
			final List<String> mails = usersConfig.getStringList(username + ".mail");
			if (mails != null && !mails.isEmpty())
			{
				user.setMails(mails);
			}
			if (!user.hasHome())
			{
				@SuppressWarnings("unchecked")
				final List<Object> vals = (List<Object>)usersConfig.getProperty(username + ".home");
				if (vals != null)
				{
					World world = ess.getServer().getWorlds().get(0);
					if (vals.size() > 5)
					{
						world = getFakeWorld((String)vals.get(5));
					}
					if (world != null)
					{
						user.setHome("home", new Location(world,
														  ((Number)vals.get(0)).doubleValue(),
														  ((Number)vals.get(1)).doubleValue(),
														  ((Number)vals.get(2)).doubleValue(),
														  ((Number)vals.get(3)).floatValue(),
														  ((Number)vals.get(4)).floatValue()));
					}
				}
			}
		}
		usersFile.renameTo(new File(usersFile.getAbsolutePath() + ".old"));
	}

	private void convertWarps()
	{
		final File warpsFolder = new File(ess.getDataFolder(), "warps");
		if (!warpsFolder.exists())
		{
			warpsFolder.mkdirs();
		}
		final File[] listOfFiles = warpsFolder.listFiles();
		if (listOfFiles.length >= 1)
		{
			for (int i = 0; i < listOfFiles.length; i++)
			{
				final String filename = listOfFiles[i].getName();
				if (listOfFiles[i].isFile() && filename.endsWith(".dat"))
				{
					try
					{
						final BufferedReader rx = new BufferedReader(new FileReader(listOfFiles[i]));
						double x, y, z;
						float yaw, pitch;
						String worldName;
						try
						{
							if (!rx.ready())
							{
								continue;
							}
							x = Double.parseDouble(rx.readLine().trim());
							if (!rx.ready())
							{
								continue;
							}
							y = Double.parseDouble(rx.readLine().trim());
							if (!rx.ready())
							{
								continue;
							}
							z = Double.parseDouble(rx.readLine().trim());
							if (!rx.ready())
							{
								continue;
							}
							yaw = Float.parseFloat(rx.readLine().trim());
							if (!rx.ready())
							{
								continue;
							}
							pitch = Float.parseFloat(rx.readLine().trim());
							worldName = rx.readLine();
						}
						finally
						{
							rx.close();
						}
						World w = null;
						for (World world : ess.getServer().getWorlds())
						{
							if (world.getEnvironment() != World.Environment.NETHER)
							{
								w = world;
								break;
							}
						}
						if (worldName != null)
						{
							worldName = worldName.trim();
							World w1 = null;
							w1 = getFakeWorld(worldName);
							if (w1 != null)
							{
								w = w1;
							}
						}
						final Location loc = new Location(w, x, y, z, yaw, pitch);
						ess.getWarps().setWarp(filename.substring(0, filename.length() - 4), loc);
						if (!listOfFiles[i].renameTo(new File(warpsFolder, filename + ".old")))
						{
							throw new Exception(_("fileRenameError", filename));
						}
					}
					catch (Exception ex)
					{
						LOGGER.log(Level.SEVERE, null, ex);
					}
				}
			}

		}
		final File warpFile = new File(ess.getDataFolder(), "warps.txt");
		if (warpFile.exists())
		{
			try
			{
				final BufferedReader rx = new BufferedReader(new FileReader(warpFile));
				try
				{
					for (String[] parts = new String[0]; rx.ready(); parts = rx.readLine().split(":"))
					{
						if (parts.length < 6)
						{
							continue;
						}
						final String name = parts[0];
						final double x = Double.parseDouble(parts[1].trim());
						final double y = Double.parseDouble(parts[2].trim());
						final double z = Double.parseDouble(parts[3].trim());
						final float yaw = Float.parseFloat(parts[4].trim());
						final float pitch = Float.parseFloat(parts[5].trim());
						if (name.isEmpty())
						{
							continue;
						}
						World w = null;
						for (World world : ess.getServer().getWorlds())
						{
							if (world.getEnvironment() != World.Environment.NETHER)
							{
								w = world;
								break;
							}
						}
						final Location loc = new Location(w, x, y, z, yaw, pitch);
						ess.getWarps().setWarp(name, loc);
						if (!warpFile.renameTo(new File(ess.getDataFolder(), "warps.txt.old")))
						{
							throw new Exception(_("fileRenameError", "warps.txt"));
						}
					}
				}
				finally
				{
					rx.close();
				}
			}
			catch (Exception ex)
			{
				LOGGER.log(Level.SEVERE, null, ex);
			}
		}
	}

	private void sanitizeAllUserFilenames()
	{
		if (doneFile.getBoolean("sanitizeAllUserFilenames", false))
		{
			return;
		}
		final File usersFolder = new File(ess.getDataFolder(), "userdata");
		if (!usersFolder.exists())
		{
			return;
		}
		final File[] listOfFiles = usersFolder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++)
		{
			final String filename = listOfFiles[i].getName();
			if (!listOfFiles[i].isFile() || !filename.endsWith(".yml"))
			{
				continue;
			}
			final String sanitizedFilename = Util.sanitizeFileName(filename.substring(0, filename.length() - 4)) + ".yml";
			if (sanitizedFilename.equals(filename))
			{
				continue;
			}
			final File tmpFile = new File(listOfFiles[i].getParentFile(), sanitizedFilename + ".tmp");
			final File newFile = new File(listOfFiles[i].getParentFile(), sanitizedFilename);
			if (!listOfFiles[i].renameTo(tmpFile))
			{
				LOGGER.log(Level.WARNING, _("userdataMoveError", filename, sanitizedFilename));
				continue;
			}
			if (newFile.exists())
			{
				LOGGER.log(Level.WARNING, _("duplicatedUserdata", filename, sanitizedFilename));
				continue;
			}
			if (!tmpFile.renameTo(newFile))
			{
				LOGGER.log(Level.WARNING, _("userdataMoveBackError", sanitizedFilename, sanitizedFilename));
			}
		}
		doneFile.setProperty("sanitizeAllUserFilenames", true);
		doneFile.save();
	}

	private World getFakeWorld(final String name)
	{
		final File bukkitDirectory = ess.getDataFolder().getParentFile().getParentFile();
		final File worldDirectory = new File(bukkitDirectory, name);
		if (worldDirectory.exists() && worldDirectory.isDirectory())
		{
			return new FakeWorld(worldDirectory.getName(), World.Environment.NORMAL);
		}
		return null;
	}

	public Location getFakeLocation(EssentialsConf config, String path)
	{
		String worldName = config.getString((path != null ? path + "." : "") + "world");
		if (worldName == null || worldName.isEmpty())
		{
			return null;
		}
		World world = getFakeWorld(worldName);
		if (world == null)
		{
			return null;
		}
		return new Location(world,
							config.getDouble((path != null ? path + "." : "") + "x", 0),
							config.getDouble((path != null ? path + "." : "") + "y", 0),
							config.getDouble((path != null ? path + "." : "") + "z", 0),
							(float)config.getDouble((path != null ? path + "." : "") + "yaw", 0),
							(float)config.getDouble((path != null ? path + "." : "") + "pitch", 0));
	}

	private void deleteOldItemsCsv()
	{
		if (doneFile.getBoolean("deleteOldItemsCsv", false))
		{
			return;
		}
		final File file = new File(ess.getDataFolder(), "items.csv");
		if (file.exists())
		{
			try
			{
				final Set<BigInteger> oldconfigs = new HashSet<BigInteger>();
				oldconfigs.add(new BigInteger("66ec40b09ac167079f558d1099e39f10", 16)); // sep 1
				oldconfigs.add(new BigInteger("34284de1ead43b0bee2aae85e75c041d", 16)); // crlf
				oldconfigs.add(new BigInteger("c33bc9b8ee003861611bbc2f48eb6f4f", 16)); // jul 24
				oldconfigs.add(new BigInteger("6ff17925430735129fc2a02f830c1daa", 16)); // crlf

				MessageDigest digest = ManagedFile.getDigest();
				final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
				final DigestInputStream dis = new DigestInputStream(bis, digest);
				final byte[] buffer = new byte[1024];
				try
				{
					while (dis.read(buffer) != -1)
					{
					}
				}
				finally
				{
					dis.close();
				}

				BigInteger hash = new BigInteger(1, digest.digest());
				if (oldconfigs.contains(hash) && !file.delete())
				{
					throw new IOException("Could not delete file " + file.toString());
				}
				doneFile.setProperty("deleteOldItemsCsv", true);
				doneFile.save();
			}
			catch (IOException ex)
			{
				Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}

	private void updateSpawnsToNewSpawnsConfig()
	{
		if (doneFile.getBoolean("updateSpawnsToNewSpawnsConfig", false))
		{
			return;
		}
		final File configFile = new File(ess.getDataFolder(), "spawn.yml");
		if (configFile.exists())
		{

			final EssentialsConf config = new EssentialsConf(configFile);
			try
			{
				config.load();
				if (!config.hasProperty("spawns"))
				{
					final Spawns spawns = new Spawns();
					Set<String> keys = config.getKeys(false);
					for (String group : keys)
					{
						Location loc = getFakeLocation(config, group);
						spawns.getSpawns().put(group.toLowerCase(Locale.ENGLISH), loc);
					}
					if (!configFile.renameTo(new File(ess.getDataFolder(), "spawn.yml.old")))
					{
						throw new Exception(_("fileRenameError", "spawn.yml"));
					}
					PrintWriter writer = new PrintWriter(configFile);
					try
					{
						new YamlStorageWriter(writer).save(spawns);
					}
					finally
					{
						writer.close();
					}
				}
			}
			catch (Exception ex)
			{
				Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
		doneFile.setProperty("updateSpawnsToNewSpawnsConfig", true);
		doneFile.save();
	}

	private void updateJailsToNewJailsConfig()
	{
		if (doneFile.getBoolean("updateJailsToNewJailsConfig", false))
		{
			return;
		}
		final File configFile = new File(ess.getDataFolder(), "jail.yml");
		if (configFile.exists())
		{

			final EssentialsConf config = new EssentialsConf(configFile);
			try
			{
				config.load();
				if (!config.hasProperty("jails"))
				{
					final com.earth2me.essentials.settings.Jails jails = new com.earth2me.essentials.settings.Jails();
					Set<String> keys = config.getKeys(false);
					for (String jailName : keys)
					{
						Location loc = getFakeLocation(config, jailName);
						jails.getJails().put(jailName.toLowerCase(Locale.ENGLISH), loc);
					}
					if (!configFile.renameTo(new File(ess.getDataFolder(), "jail.yml.old")))
					{
						throw new Exception(_("fileRenameError", "jail.yml"));
					}
					PrintWriter writer = new PrintWriter(configFile);
					try
					{
						new YamlStorageWriter(writer).save(jails);
					}
					finally
					{
						writer.close();
					}
				}
			}
			catch (Exception ex)
			{
				Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
		doneFile.setProperty("updateJailsToNewJailsConfig", true);
		doneFile.save();
	}

	private void warnMetrics()
	{
		if (doneFile.getBoolean("warnMetrics", false))
		{
			return;
		}
		ess.getSettings().setMetricsEnabled(false);
		doneFile.setProperty("warnMetrics", true);
		doneFile.save();
	}

	public void beforeSettings()
	{
		if (!ess.getDataFolder().exists())
		{
			ess.getDataFolder().mkdirs();
		}
		moveWorthValuesToWorthYml();
		moveMotdRulesToFile("motd");
		moveMotdRulesToFile("rules");
	}

	public void afterSettings()
	{
		sanitizeAllUserFilenames();
		updateUsersToNewDefaultHome();
		moveUsersDataToUserdataFolder();
		convertWarps();
		updateUsersPowerToolsFormat();
		updateUsersHomesFormat();
		deleteOldItemsCsv();
		updateSpawnsToNewSpawnsConfig();
		updateJailsToNewJailsConfig();
		warnMetrics();
	}
}
