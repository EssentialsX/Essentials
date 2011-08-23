package com.earth2me.essentials;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
			LOGGER.log(Level.SEVERE, Util.i18n("upgradingFilesError"), e);
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
				throw new Exception(Util.i18n("configFileMoveError"));
			}
			if (!tempFile.renameTo(file))
			{
				throw new Exception(Util.i18n("configFileRenameError"));
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

						final String worldName = world.getName().toLowerCase();
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
					final Map<Integer, Object> powertools = (Map<Integer, Object>)config.getProperty("powertools");
					if (powertools == null)
					{
						continue;
					}
					for (Map.Entry<Integer, Object> entry : powertools.entrySet())
					{
						if (entry.getValue() instanceof String)
						{
							List<String> temp = new ArrayList<String>();
							temp.add((String)entry.getValue());
							((Map<Integer, Object>)powertools).put(entry.getKey(), temp);
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
					final Location defloc = config.getLocation("home.worlds." + defworld, ess.getServer());

					String worldName = defloc.getWorld().getName().toLowerCase();
					config.setProperty("homes.home", defloc);

					List<String> worlds = config.getKeys("home.worlds");
					Location loc;

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
						loc = config.getLocation("home.worlds." + world, ess.getServer());
						if (loc == null)
						{
							continue;
						}
						worldName = loc.getWorld().getName().toLowerCase();
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
		for (String username : usersConfig.getKeys(null))
		{
			final User user = new User(new OfflinePlayer(username, ess), ess);
			final String nickname = usersConfig.getString(username + ".nickname");
			if (nickname != null && !nickname.isEmpty() && !nickname.equals(username))
			{
				user.setNickname(nickname);
			}
			final List<String> mails = usersConfig.getStringList(username + ".mail", null);
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
							throw new Exception(Util.format("fileRenameError", filename));
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
							throw new Exception(Util.format("fileRenameError", "warps.txt"));
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
				LOGGER.log(Level.WARNING, Util.format("userdataMoveError", filename, sanitizedFilename));
				continue;
			}
			if (newFile.exists())
			{
				LOGGER.log(Level.WARNING, Util.format("duplicatedUserdata", filename, sanitizedFilename));
				continue;
			}
			if (!tmpFile.renameTo(newFile))
			{
				LOGGER.log(Level.WARNING, Util.format("userdataMoveBackError", sanitizedFilename, sanitizedFilename));
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

	public void beforeSettings()
	{
		if (!ess.getDataFolder().exists())
		{
			ess.getDataFolder().mkdirs();
		}
		moveWorthValuesToWorthYml();
	}

	public void afterSettings()
	{
		sanitizeAllUserFilenames();
		updateUsersToNewDefaultHome();
		moveUsersDataToUserdataFolder();
		convertWarps();
		updateUsersPowerToolsFormat();
		updateUsersHomesFormat();
	}
}
