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
import com.earth2me.essentials.register.payment.Methods;
import com.earth2me.essentials.signs.SignBlockListener;
import com.earth2me.essentials.signs.SignEntityListener;
import com.earth2me.essentials.signs.SignPlayerListener;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.scheduler.CraftScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;


public class Essentials extends JavaPlugin implements IEssentials
{
	public static final String AUTHORS = "Zenexer, ementalo, Aelux, Brettflan, KimKandor, snowleo, ceulemans and Xeology";
	public static final int minBukkitBuildVersion = 974;
	private static final Logger logger = Logger.getLogger("Minecraft");
	private Settings settings;
	private TNTExplodeListener tntListener;
	private EssentialsDependancyChecker essDep;
	private static Essentials instance = null;
	private Spawn spawn;
	private Jail jail;
	private Warps warps;
	private Worth worth;
	private List<IConf> confList;
	public ArrayList bans = new ArrayList();
	public ArrayList bannedIps = new ArrayList();
	private Backup backup;
	private final Map<String, User> users = new HashMap<String, User>();
	private EssentialsUpdateTimer updateTimer;
	private boolean registerFallback = true;
	private final Methods paymentMethod = new Methods();
	private final static boolean enableErrorLogging = false;
	private final EssentialsErrorHandler errorHandler = new EssentialsErrorHandler();
	private IPermissionsHandler permissionsHandler;

	public static IEssentials getStatic()
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
		permissionsHandler = new ConfigPermissionsHandler(this);
		setStatic();
	}

	public void setStatic()
	{
		instance = this;
	}

	public void onEnable()
	{
		final String[] javaversion = System.getProperty("java.version").split("\\.", 3);
		if (javaversion == null || javaversion.length < 2 || Integer.parseInt(javaversion[1]) < 6)
		{
			logger.log(Level.SEVERE, "Java version not supported! Please install Java 1.6. You have " + System.getProperty("java.version"));
		}
		if (enableErrorLogging)
		{
			logger.addHandler(errorHandler);
		}
		setStatic();
		EssentialsUpgrade upgrade = new EssentialsUpgrade(this.getDescription().getVersion(), this);
		upgrade.beforeSettings();
		confList = new ArrayList<IConf>();
		settings = new Settings(this.getDataFolder());
		confList.add(settings);
		upgrade.afterSettings();
		Util.updateLocale(settings.getLocale(), this.getDataFolder());
		spawn = new Spawn(getServer(), this.getDataFolder());
		confList.add(spawn);
		warps = new Warps(getServer(), this.getDataFolder());
		confList.add(warps);
		worth = new Worth(this.getDataFolder());
		confList.add(worth);
		reload();
		backup = new Backup(this);
		essDep = new EssentialsDependancyChecker(this);

		final PluginManager pm = getServer().getPluginManager();
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

		Plugin permissionsPlugin = pm.getPlugin("Permissions");

		if (permissionsPlugin != null)
		{
			if (permissionsPlugin.getDescription().getVersion().charAt(0) == '3')
			{
				this.permissionsHandler = new Permissions3Handler(permissionsPlugin);
			}
			else
			{
				this.permissionsHandler = new Permissions2Handler(permissionsPlugin);
			}
		}
		else
		{
			this.permissionsHandler = new ConfigPermissionsHandler(this);
		}

		final ServerListener serverListener = new EssentialsPluginListener(paymentMethod);
		pm.registerEvent(Type.PLUGIN_ENABLE, serverListener, Priority.Low, this);
		pm.registerEvent(Type.PLUGIN_DISABLE, serverListener, Priority.Low, this);

		final EssentialsPlayerListener playerListener = new EssentialsPlayerListener(this);
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Lowest, this);
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

		final EssentialsBlockListener blockListener = new EssentialsBlockListener(this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Lowest, this);

		final SignBlockListener signBlockListener = new SignBlockListener(this);
		pm.registerEvent(Type.SIGN_CHANGE, signBlockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_PLACE, signBlockListener, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_BREAK, signBlockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_IGNITE, signBlockListener, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_BURN, signBlockListener, Priority.Low, this);

		final SignPlayerListener signPlayerListener = new SignPlayerListener(this);
		pm.registerEvent(Type.PLAYER_INTERACT, signPlayerListener, Priority.Low, this);

		final SignEntityListener signEntityListener = new SignEntityListener(this);
		pm.registerEvent(Type.ENTITY_EXPLODE, signEntityListener, Priority.Low, this);

		final EssentialsEntityListener entityListener = new EssentialsEntityListener(this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_COMBUST, entityListener, Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Lowest, this);

		jail = new Jail(this);
		final JailPlayerListener jailPlayerListener = new JailPlayerListener(this);
		confList.add(jail);
		pm.registerEvent(Type.BLOCK_BREAK, jail, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_DAMAGE, jail, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_PLACE, jail, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_INTERACT, jailPlayerListener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_RESPAWN, jailPlayerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, jailPlayerListener, Priority.High, this);

		if (settings.isNetherEnabled() && getServer().getWorlds().size() < 2)
		{
			getServer().createWorld(settings.getNetherName(), World.Environment.NETHER);
		}

		tntListener = new TNTExplodeListener(this);
		pm.registerEvent(Type.ENTITY_EXPLODE, tntListener, Priority.High, this);

		final EssentialsTimer timer = new EssentialsTimer(this);
		getScheduler().scheduleSyncRepeatingTask(this, timer, 1, 50);
		if (enableErrorLogging)
		{
			updateTimer = new EssentialsUpdateTimer(this);
			getScheduler().scheduleAsyncRepeatingTask(this, updateTimer, 50, 50 * 60 * (this.getDescription().getVersion().startsWith("Dev") ? 60 : 360));
		}
		logger.info(Util.format("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), AUTHORS));
	}

	public void onDisable()
	{
		instance = null;
		Trade.closeLog();
		logger.removeHandler(errorHandler);
	}

	public void reload()
	{
		Trade.closeLog();
		loadBanList();

		for (IConf iConf : confList)
		{
			iConf.reloadConfig();
		}

		Util.updateLocale(settings.getLocale(), this.getDataFolder());

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

		if (lines.isEmpty() || lines.get(0) == null)
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

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		return onCommandEssentials(sender, command, commandLabel, args, Essentials.class.getClassLoader(), "com.earth2me.essentials.commands.Command", "essentials.");
	}

	public boolean onCommandEssentials(CommandSender sender, Command command, String commandLabel, String[] args, ClassLoader classLoader, String commandPath, String permissionPrefix)
	{
		// Allow plugins to override the command via onCommand
		if (!getSettings().isCommandOverridden(command.getName()) && !commandLabel.startsWith("e"))
		{
			for (Plugin p : getServer().getPluginManager().getPlugins())
			{
				if (p.getDescription().getMain().contains("com.earth2me.essentials"))
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

				PluginCommand pc = getServer().getPluginCommand(desc.getName() + ":" + commandLabel);
				if (pc != null)
				{
					return pc.execute(sender, commandLabel, args);
				}
			}
		}

		try
		{
			User user = null;
			if (sender instanceof Player)
			{
				user = getUser(sender);
				logger.log(Level.INFO, String.format("[PLAYER_COMMAND] %s: /%s %s ", ((Player)sender).getName(), commandLabel, EssentialsCommand.getFinalArg(args, 0)));
			}

			// New mail notification
			if (user != null && !getSettings().isCommandDisabled("mail") && !commandLabel.equals("mail") && user.isAuthorized("essentials.mail"))
			{
				final List<String> mail = user.getMails();
				if (mail != null && !mail.isEmpty())
				{
					user.sendMessage(Util.format("youHaveNewMail", mail.size()));
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
				cmd.setEssentials(this);
			}
			catch (Exception ex)
			{
				sender.sendMessage(Util.format("commandNotLoaded", commandLabel));
				logger.log(Level.SEVERE, Util.format("commandNotLoaded", commandLabel), ex);
				return true;
			}

			// Check authorization
			if (user != null && !user.isAuthorized(cmd, permissionPrefix))
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
				sender.sendMessage(command.getUsage().replaceAll("<command>", commandLabel));
				return true;
			}
			catch (Throwable ex)
			{
				showError(sender, ex, commandLabel);
				return true;
			}
		}
		catch (Throwable ex)
		{
			logger.log(Level.SEVERE, Util.format("commandFailed", commandLabel), ex);
			return true;
		}
	}

	public void showError(final CommandSender sender, final Throwable exception, final String commandLabel)
	{
		sender.sendMessage(Util.format("errorWithMessage", exception.getMessage()));
		final LogRecord logRecord = new LogRecord(Level.WARNING, Util.format("errorCallingCommand", commandLabel));
		logRecord.setThrown(exception);
		if (getSettings().isDebug())
		{
			logger.log(logRecord);
		}
		else
		{
			if (enableErrorLogging)
			{
				errorHandler.publish(logRecord);
				errorHandler.flush();
			}
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

			final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			try
			{
				bans.clear();
				while (bufferedReader.ready())
				{

					final String line = bufferedReader.readLine().trim().toLowerCase();
					if (line.length() > 0 && line.charAt(0) == '#')
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
			finally
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException ex)
				{
					logger.log(Level.SEVERE, Util.i18n("bannedPlayersFileError"), ex);
				}
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

			final BufferedReader bufferedReader = new BufferedReader(new FileReader(ipFile));
			try
			{
				bannedIps.clear();
				while (bufferedReader.ready())
				{

					final String line = bufferedReader.readLine().trim().toLowerCase();
					if (line.length() > 0 && line.charAt(0) == '#')
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
			finally
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException ex)
				{
					logger.log(Level.SEVERE, Util.i18n("bannedIpsFileError"), ex);
				}
			}
		}
		catch (FileNotFoundException ex)
		{
			logger.log(Level.SEVERE, Util.i18n("bannedIpsFileError"), ex);
		}
	}

	public CraftScheduler getScheduler()
	{
		return (CraftScheduler)this.getServer().getScheduler();
	}

	public Jail getJail()
	{
		return jail;
	}

	public Warps getWarps()
	{
		return warps;
	}

	public Worth getWorth()
	{
		return worth;
	}

	public Backup getBackup()
	{
		return backup;
	}

	public Spawn getSpawn()
	{
		return spawn;
	}

	public User getUser(Object base)
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

		if (users.containsKey(base.getName().toLowerCase()))
		{
			return users.get(base.getName().toLowerCase()).update(base);
		}

		User u = new User(base, this);
		users.put(u.getName().toLowerCase(), u);
		return u;
	}

	public Map<String, User> getAllUsers()
	{
		return users;
	}

	public User getOfflineUser(String name)
	{
		File userFolder = new File(getDataFolder(), "userdata");
		File userFile = new File(userFolder, Util.sanitizeFileName(name) + ".yml");
		if (userFile.exists())
		{	//Users do not get offline changes saved without being reproccessed as Users! ~ Xeology :)
			return getUser((Player)new OfflinePlayer(name));

		}
		return null;
	}

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
		return null;
	}

	public void setRegisterFallback(boolean registerFallback)
	{
		this.registerFallback = registerFallback;
	}

	public boolean isRegisterFallbackEnabled()
	{
		return registerFallback;
	}

	public void addReloadListener(IConf listener)
	{
		confList.add(listener);
	}

	public Methods getPaymentMethod()
	{
		return paymentMethod;
	}

	public int broadcastMessage(String name, String message)
	{
		Player[] players = getServer().getOnlinePlayers();

		for (Player player : players)
		{
			User u = getUser(player);
			if (!u.isIgnoredPlayer(name))
			{
				player.sendMessage(message);
			}
		}

		return players.length;
	}

	public Map<BigInteger, String> getErrors()
	{
		return errorHandler.getErrors();
	}

	public int scheduleAsyncDelayedTask(final Runnable run)
	{
		return this.getScheduler().scheduleAsyncDelayedTask(this, run);
	}

	public int scheduleSyncDelayedTask(final Runnable run)
	{
		return this.getScheduler().scheduleSyncDelayedTask(this, run);
	}

	public int scheduleSyncDelayedTask(final Runnable run, final long delay)
	{
		return this.getScheduler().scheduleSyncDelayedTask(this, run, delay);
	}

	public int scheduleSyncRepeatingTask(final Runnable run, long delay, long period)
	{
		return this.getScheduler().scheduleSyncRepeatingTask(this, run, delay, period);
	}

	public List<String> getBans()
	{
		return bans;
	}

	public List<String> getBannedIps()
	{
		return bannedIps;
	}

	public TNTExplodeListener getTNTListener()
	{
		return tntListener;
	}

	public EssentialsDependancyChecker getDependancyChecker()
	{
		return essDep;
	}

	public IPermissionsHandler getPermissionsHandler()
	{
		return permissionsHandler;
	}
}
