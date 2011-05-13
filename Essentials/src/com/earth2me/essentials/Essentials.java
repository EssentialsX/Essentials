/*
 * Essentials - a bukkit plugin
 * Copyright (C) 2011  Essentials Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.earth2me.essentials;

import com.earth2me.essentials.commands.EssentialsCommand;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.scheduler.CraftScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;


public class Essentials extends JavaPlugin
{
	public static final String AUTHORS = "Zenexer, ementalo, Aelux, Brettflan, KimKandor, snowleo, ceulemans and Xeology";
	public static final int minBukkitBuildVersion = 766;
	private static final Logger logger = Logger.getLogger("Minecraft");
	private Settings settings;
	private EssentialsPlayerListener playerListener;
	private EssentialsBlockListener blockListener;
	private EssentialsEntityListener entityListener;
	private JailPlayerListener jailPlayerListener;
	private static Essentials instance = null;
	private Spawn spawn;
	private Jail jail;
	private Warps warps;
	private Worth worth;
	private List<IConf> confList;
	public ArrayList bans = new ArrayList();
	public ArrayList bannedIps = new ArrayList();
	private Backup backup;
	private Map<String, User> users = new HashMap<String, User>();
	private EssentialsTimer timer;
	private boolean iConomyFallback = true;

	public Essentials()
	{
	}

	public static Essentials getStatic()
	{
		return instance;
	}

	public Settings getSettings()
	{
		return settings;
	}

	public void setupForTesting(Server server) throws IOException, InvalidDescriptionException
	{
		File dataFolder = File.createTempFile("essentialstest", "");
		dataFolder.delete();
		dataFolder.mkdir();
		logger.log(Level.INFO, Util.i18n("usingTempFolderForTesting"));
		logger.log(Level.INFO, dataFolder.toString());
		this.initialize(null, server, new PluginDescriptionFile(new FileReader(new File("src" + File.separator + "plugin.yml"))), dataFolder, null, null);
		settings = new Settings(dataFolder);
		setStatic();
	}

	public void setStatic()
	{
		instance = this;
	}

	public void onEnable()
	{
		setStatic();
		EssentialsUpgrade upgrade = new EssentialsUpgrade(this.getDescription().getVersion(), this);
		if (newWorldsLoaded)
		{
			logger.log(Level.SEVERE, Util.i18n("worldsLoadedRestartServer"));
			try
			{
				getServer().dispatchCommand(Console.getCommandSender(getServer()), "stop");
			}
			catch (Exception ex)
			{
				logger.log(Level.SEVERE, Util.i18n("failedStopServer"), ex);
			}
		}
		confList = new ArrayList<IConf>();
		settings = new Settings(this.getDataFolder());
		confList.add(settings);
		Util.updateLocale(settings.getLocale(), this.getDataFolder());
		spawn = new Spawn(getServer(), this.getDataFolder());
		confList.add(spawn);
		warps = new Warps(getServer(), this.getDataFolder());
		confList.add(warps);
		worth = new Worth(this.getDataFolder());
		confList.add(worth);
		reload();
		backup = new Backup();

		PluginManager pm = getServer().getPluginManager();
		for (Plugin plugin : pm.getPlugins())
		{
			if (plugin.getDescription().getName().startsWith("Essentials"))
			{
				if (!plugin.getDescription().getVersion().equals(this.getDescription().getVersion()))
				{
					logger.log(Level.WARNING, Util.format("versionMismatch", plugin.getDescription().getName()));
				}
			}
		}
		Matcher versionMatch = Pattern.compile("git-Bukkit-([0-9]+).([0-9]+).([0-9]+)-[0-9]+-[0-9a-z]+-b([0-9]+)jnks.*").matcher(getServer().getVersion());
		if (versionMatch.matches())
		{
			int versionNumber = Integer.parseInt(versionMatch.group(4));
			if (versionNumber < minBukkitBuildVersion)
			{
				logger.log(Level.WARNING, Util.i18n("notRecommendedBukkit"));
			}
		}
		else
		{
			logger.log(Level.INFO, Util.i18n("bukkitFormatChanged"));
		}


		playerListener = new EssentialsPlayerListener(this);
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Lowest, this);
		if (getSettings().getNetherPortalsEnabled())
		{
			pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.High, this);
		}
		pm.registerEvent(Type.PLAYER_LOGIN, playerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_EGG_THROW, playerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_BUCKET_EMPTY, playerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_ANIMATION, playerListener, Priority.High, this);

		blockListener = new EssentialsBlockListener(this);
		pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Lowest, this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Lowest, this);

		entityListener = new EssentialsEntityListener(this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_COMBUST, entityListener, Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Lowest, this);

		jail = new Jail(this);
		jailPlayerListener = new JailPlayerListener(this);
		confList.add(jail);
		pm.registerEvent(Type.BLOCK_BREAK, jail, Priority.High, this);
		pm.registerEvent(Type.BLOCK_DAMAGE, jail, Priority.High, this);
		pm.registerEvent(Type.BLOCK_PLACE, jail, Priority.High, this);
		pm.registerEvent(Type.PLAYER_INTERACT, jailPlayerListener, Priority.High, this);
		attachEcoListeners();

		if (settings.isNetherEnabled() && getServer().getWorlds().size() < 2)
		{
			getServer().createWorld(settings.getNetherName(), World.Environment.NETHER);
		}

		timer = new EssentialsTimer(this);
		getScheduler().scheduleSyncRepeatingTask(this, timer, 1, 50);

		logger.info(Util.format("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), AUTHORS));
	}

	public void onDisable()
	{
		instance = null;
	}

	public void reload()
	{
		loadBanList();

		for (IConf iConf : confList)
		{
			iConf.reloadConfig();
		}
		
		for (User user : users.values())
		{
			user.reloadConfig();
		}
		
		// for motd
		getConfiguration().load();

		try
		{
			ItemDb.load(getDataFolder(), "items.csv");
		}
		catch (Exception ex)
		{
			logger.log(Level.WARNING, Util.i18n("itemsCsvNotLoaded"), ex);
		}
	}

	public String[] getMotd(CommandSender sender, String def)
	{
		return getLines(sender, "motd", def);
	}

	public String[] getLines(CommandSender sender, String node, String def)
	{
		List<String> lines = (List<String>)getConfiguration().getProperty(node);
		if (lines == null)
		{
			return new String[0];
		}
		String[] retval = new String[lines.size()];

		if (lines == null || lines.isEmpty() || lines.get(0) == null)
		{
			try
			{
				lines = new ArrayList<String>();
				// "[]" in YaML indicates empty array, so respect that
				if (!getConfiguration().getString(node, def).equals("[]"))
				{
					lines.add(getConfiguration().getString(node, def));
					retval = new String[lines.size()];
				}
			}
			catch (Throwable ex2)
			{
				logger.log(Level.WARNING, Util.format("corruptNodeInConfig", node));
				return new String[0];
			}
		}

		// if still empty, call it a day
		if (lines == null || lines.isEmpty() || lines.get(0) == null)
		{
			return new String[0];
		}

		for (int i = 0; i < lines.size(); i++)
		{
			String m = lines.get(i);
			if (m == null)
			{
				continue;
			}
			m = m.replace('&', '§').replace("§§", "&");

			if (sender instanceof User || sender instanceof Player)
			{
				User user = getUser(sender);
				m = m.replace("{PLAYER}", user.getDisplayName());
				m = m.replace("{IP}", user.getAddress().toString());
				m = m.replace("{BALANCE}", Double.toString(user.getMoney()));
				m = m.replace("{MAILS}", Integer.toString(user.getMails().size()));
			}

			m = m.replace("{ONLINE}", Integer.toString(getServer().getOnlinePlayers().length));

			if (m.matches(".*\\{PLAYERLIST\\}.*"))
			{
				StringBuilder online = new StringBuilder();
				for (Player p : getServer().getOnlinePlayers())
				{
					if (online.length() > 0)
					{
						online.append(", ");
					}
					online.append(p.getDisplayName());
				}
				m = m.replace("{PLAYERLIST}", online.toString());
			}

			if (sender instanceof Player)
			{
				try
				{
					Class User = getClassLoader().loadClass("bukkit.Vandolis.User");
					Object vuser = User.getConstructor(User.class).newInstance((Player)sender);
					m = m.replace("{RED:BALANCE}", User.getMethod("getMoney").invoke(vuser).toString());
					m = m.replace("{RED:BUYS}", User.getMethod("getNumTransactionsBuy").invoke(vuser).toString());
					m = m.replace("{RED:SELLS}", User.getMethod("getNumTransactionsSell").invoke(vuser).toString());
				}
				catch (Throwable ex)
				{
					m = m.replace("{RED:BALANCE}", "N/A");
					m = m.replace("{RED:BUYS}", "N/A");
					m = m.replace("{RED:SELLS}", "N/A");
				}
			}

			retval[i] = m + " ";
		}
		return retval;
	}

	@SuppressWarnings("LoggerStringConcat")
	public static void previewCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if (sender instanceof Player)
		{
			logger.info(ChatColor.BLUE + "[PLAYER_COMMAND] " + ((Player)sender).getName() + ": /" + commandLabel + " " + EssentialsCommand.getFinalArg(args, 0));
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		return onCommandEssentials(sender, command, commandLabel, args, Essentials.class.getClassLoader(), "com.earth2me.essentials.commands.Command");
	}
	
	public boolean onCommandEssentials(CommandSender sender, Command command, String commandLabel, String[] args, ClassLoader classLoader, String commandPath)
	{
		if ("msg".equals(commandLabel.toLowerCase()) || "mail".equals(commandLabel.toLowerCase()) &  sender instanceof CraftPlayer)
		{
			StringBuilder str = new StringBuilder();
			str.append(commandLabel + " ");
			for (String a : args)
			{
				str.append(a + " ");
			}
			for (Player player : getServer().getOnlinePlayers())
			{
				if (getUser(player).isSocialSpyEnabled())
				{
					player.sendMessage(getUser(sender).getDisplayName() + " : " + str);
				}
			}
		}
		// Allow plugins to override the command via onCommand
		if (!getSettings().isCommandOverridden(command.getName()) && !commandLabel.startsWith("e"))
		{
			for (Plugin p : getServer().getPluginManager().getPlugins())
			{
				if (p == this)
				{
					continue;
				}

				PluginDescriptionFile desc = p.getDescription();
				if (desc == null)
				{
					continue;
				}

				if (desc.getName() == null)
				{
					continue;
				}

				if (!(desc.getCommands() instanceof Map))
				{
					continue;
				}

				Map<String, Object> cmds = (Map<String, Object>)desc.getCommands();
				if (!cmds.containsKey(command.getName()))
				{
					continue;
				}

				PluginCommand pcmd = getServer().getPluginCommand(desc.getName() + ":" + commandLabel);

				if (pcmd == null)
				{
					continue;
				}

				return getServer().getPluginCommand(p.getDescription().getName() + ":" + commandLabel).execute(sender, commandLabel, args);
			}
		}

		try
		{
			previewCommand(sender, command, commandLabel, args);
			User user = sender instanceof Player ? getUser(sender) : null;

			// New mail notification
			if (user != null && !getSettings().isCommandDisabled("mail") && !commandLabel.equals("mail") && user.isAuthorized("essentials.mail"))
			{
				List<String> mail = user.getMails();
				if (mail != null)
				{
					if (mail.size() > 0)
					{
						user.sendMessage(Util.format("youHaveNewMail", mail.size()));
					}
				}
			}

			// Check for disabled commands
			if (getSettings().isCommandDisabled(commandLabel))
			{
				return true;
			}

			IEssentialsCommand cmd;
			try
			{
				cmd = (IEssentialsCommand)classLoader.loadClass(commandPath + command.getName()).newInstance();
			}
			catch (Exception ex)
			{
				sender.sendMessage(Util.format("commandNotLoaded", commandLabel));
				logger.log(Level.SEVERE, Util.format("commandNotLoaded", commandLabel), ex);
				return true;
			}

			// Check authorization
			if (user != null && !user.isAuthorized(cmd))
			{
				logger.log(Level.WARNING, Util.format("deniedAccessCommand", user.getName()));
				user.sendMessage(Util.i18n("noAccessCommand"));
				return true;
			}

			// Run the command
			try
			{
				if (user == null)
				{
					cmd.run(getServer(), sender, commandLabel, command, args);
				}
				else
				{
					cmd.run(getServer(), user, commandLabel, command, args);
				}
				return true;
			}
			catch (NotEnoughArgumentsException ex)
			{
				sender.sendMessage(command.getDescription());
				sender.sendMessage(command.getUsage());
				return true;
			}
			catch (Throwable ex)
			{
				sender.sendMessage(ChatColor.RED + "Error: " + ex.getMessage());
				if (getSettings().isDebug())
				{
					logger.log(Level.WARNING, Util.format("errorCallingCommand", commandLabel), ex);
				}
				return true;
			}
		}
		catch (Throwable ex)
		{
			logger.log(Level.SEVERE, Util.format("commandFailed", commandLabel), ex);
			return true;
		}
	}

	public void loadBanList()
	{
		//I don't like this but it needs to be done until CB fixors
		File file = new File("banned-players.txt");
		File ipFile = new File("banned-ips.txt");
		try
		{
			if (!file.exists())
			{
				throw new FileNotFoundException(Util.i18n("bannedPlayersFileNotFound"));
			}

			BufferedReader rx = new BufferedReader(new FileReader(file));
			bans.clear();
			try
			{
				for (int i = 0; rx.ready(); i++)
				{

					String line = rx.readLine().trim().toLowerCase();
					if (line.startsWith("#"))
					{
						continue;
					}
					bans.add(line);

				}
			}
			catch (IOException io)
			{
				logger.log(Level.SEVERE, Util.i18n("bannedPlayersFileError"), io);
			}
		}
		catch (FileNotFoundException ex)
		{
			logger.log(Level.SEVERE, Util.i18n("bannedPlayersFileError"), ex);
		}

		try
		{
			if (!ipFile.exists())
			{
				throw new FileNotFoundException(Util.i18n("bannedIpsFileNotFound"));
			}

			BufferedReader rx = new BufferedReader(new FileReader(ipFile));
			bannedIps.clear();
			try
			{
				for (int i = 0; rx.ready(); i++)
				{

					String line = rx.readLine().trim().toLowerCase();
					if (line.startsWith("#"))
					{
						continue;
					}
					bannedIps.add(line);

				}
			}
			catch (IOException io)
			{
				logger.log(Level.SEVERE, Util.i18n("bannedIpsFileError"), io);
			}
		}
		catch (FileNotFoundException ex)
		{
			logger.log(Level.SEVERE, Util.i18n("bannedIpsFileError"), ex);
		}
	}

	private void attachEcoListeners()
	{
		PluginManager pm = getServer().getPluginManager();
		EssentialsEcoBlockListener ecoBlockListener = new EssentialsEcoBlockListener(this);
		EssentialsEcoPlayerListener ecoPlayerListener = new EssentialsEcoPlayerListener(this);
		pm.registerEvent(Type.PLAYER_INTERACT, ecoPlayerListener, Priority.High, this);
		pm.registerEvent(Type.BLOCK_BREAK, ecoBlockListener, Priority.High, this);
		pm.registerEvent(Type.SIGN_CHANGE, ecoBlockListener, Priority.Monitor, this);
	}

	public CraftScheduler getScheduler()
	{
		return (CraftScheduler)this.getServer().getScheduler();
	}

	public static Jail getJail()
	{
		return getStatic().jail;
	}

	public static Warps getWarps()
	{
		return getStatic().warps;
	}

	public static Worth getWorth()
	{
		return getStatic().worth;
	}

	public static Backup getBackup()
	{
		return getStatic().backup;
	}

	public static Spawn getSpawn()
	{
		return getStatic().spawn;
	}

	public <T> User getUser(T base)
	{
		if (base instanceof Player)
		{
			return getUser((Player)base);
		}
		return null;
	}

	private <T extends Player> User getUser(T base)
	{
		if (base == null)
		{
			return null;
		}

		if (base instanceof User)
		{
			return (User)base;
		}

		if (users.containsKey(base.getName()))
		{
			return users.get(base.getName()).update(base);
		}

		User u = new User(base, this);
		users.put(u.getName(), u);
		return u;
	}

	public User getOfflineUser(String name)
	{
		File userFolder = new File(getDataFolder(), "userdata");
		File userFile = new File(userFolder, Util.sanitizeFileName(name) + ".yml");
		if (userFile.exists())
		{	//Users do not get offline changes saved without being reproccessed as Users! ~ Xeology :)
			return getUser((Player) new OfflinePlayer(name));
			
		}
		return null;
	}
	private boolean newWorldsLoaded = false;

	public World getWorld(String name)
	{
		if (name.matches("[0-9]+"))
		{
			int id = Integer.parseInt(name);
			if (id < getServer().getWorlds().size())
			{
				return getServer().getWorlds().get(id);
			}
		}
		World w = getServer().getWorld(name);
		if (w != null)
		{
			return w;
		}
		File bukkitDirectory = getStatic().getDataFolder().getParentFile().getParentFile();
		File worldDirectory = new File(bukkitDirectory, name);
		if (worldDirectory.exists() && worldDirectory.isDirectory())
		{
			w = getServer().createWorld(name, World.Environment.NORMAL);
			if (w != null)
			{
				newWorldsLoaded = true;
			}
			return w;
		}
		return null;
	}

	public void setIConomyFallback(boolean iConomyFallback)
	{
		this.iConomyFallback = iConomyFallback;
	}

	public boolean isIConomyFallbackEnabled()
	{
		return iConomyFallback;
	}
	
	public void addReloadListener(IConf listener)
	{
		confList.add(listener);
	}
}
