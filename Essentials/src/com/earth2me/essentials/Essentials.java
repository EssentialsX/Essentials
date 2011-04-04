package com.earth2me.essentials;

import com.earth2me.essentials.commands.EssentialsCommand;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.commands.IEssentialsCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.scheduler.CraftScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;
import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;


public class Essentials extends JavaPlugin
{
	public static final String AUTHORS = "Zenexer, ementalo, Aelux, Brettflan, KimKandor, snowleo and ceulemans.";
	public static final int minBukkitBuildVersion = 617;
	private static final Logger logger = Logger.getLogger("Minecraft");
	private static final Yaml yaml = new Yaml(new SafeConstructor());
	private static Map<String, Object> users;
	private static Settings settings;
	private static final Object usersLock = new Object();
	public static Object permissions = null;
	public final Map<User, User> tpcRequests = new HashMap<User, User>();
	public final Map<User, Boolean> tpcHere = new HashMap<User, Boolean>();
	public final List<User> away = new ArrayList<User>();
	private EssentialsPlayerListener playerListener;
	private EssentialsBlockListener blockListener;
	private EssentialsEntityListener entityListener;
	private JailPlayerListener jailPlayerListener;
	private static Essentials staticThis = null;
	public Spawn spawn;
	private Jail jail;
	private Warps warps;
	private List<IConf> confList;
	public ArrayList bans = new ArrayList();
	public ArrayList bannedIps = new ArrayList();
	public Backup backup;

	public Essentials() throws IOException
	{
		loadClasses();
	}

	public static void ensureEnabled(Server server)
	{
		PluginManager pm = server.getPluginManager();
		Essentials ess = (Essentials)pm.getPlugin("Essentials");
		if (!ess.isEnabled())
			pm.enablePlugin(ess);
		loadClasses();
	}

	@SuppressWarnings("CallToThreadDumpStack")
	public static void loadClasses()
	{
		final String[] classes = new String[]
		{
			"commands.IEssentialsCommand",
			"commands.EssentialsCommand",
			"User",
			"TargetBlock",
			"Spawn",
			"Settings",
			"OfflinePlayer",
			"ItemDb",
			"Mob",
			"EssentialsBlockListener"
		};

		try
		{
			for (String c : classes)
				Essentials.class.getClassLoader().loadClass("com.earth2me.essentials." + c);
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
	}

	public static Essentials getStatic()
	{
		return staticThis;
	}

	public static Settings getSettings()
	{
		return settings;
	}

	public void setupPermissions()
	{
		Plugin permPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		if (permissions == null && permPlugin != null) permissions = permPlugin;
	}

	public Player getPlayer(String[] args, int pos)
			throws IndexOutOfBoundsException, NoSuchFieldException
	{
		if (args.length <= pos) throw new IndexOutOfBoundsException("§cInvalid command syntax. Did you forget an argument?");
		List<Player> matches = getServer().matchPlayer(args[0]);
		if (matches.size() < 1) throw new NoSuchFieldException("§cNo matching players could be found.");
		return matches.get(0);
	}

	public void setStatic()
	{
		staticThis = this;
	}

	@SuppressWarnings("LoggerStringConcat")
	public void onEnable()
	{
		setStatic();
		confList = new ArrayList<IConf>();
		settings = new Settings(this.getDataFolder());
		confList.add(settings);
		this.spawn = new Spawn(getServer(), this.getDataFolder());
		confList.add(spawn);
		warps = new Warps(getServer(), this.getDataFolder());
		confList.add(warps);
		reload();
		this.backup = new Backup();

		PluginManager pm = getServer().getPluginManager();
		for (Plugin plugin : pm.getPlugins()) {
			if (plugin.getDescription().getName().startsWith("Essentials")) {
				if (!plugin.getDescription().getVersion().equals(this.getDescription().getVersion())) {
					logger.log(Level.WARNING, "Version mismatch! Please update "+plugin.getDescription().getName()+" to the same version.");
				}
			}
		}
		Matcher versionMatch = Pattern.compile("git-Bukkit-([0-9]+).([0-9]+).([0-9]+)-[0-9]+-[0-9a-z]+-b([0-9]+)jnks.*").matcher(getServer().getVersion());
		if (versionMatch.matches()) {
			int versionNumber = Integer.parseInt(versionMatch.group(4));
			if (versionNumber < minBukkitBuildVersion) {
				logger.log(Level.WARNING, "Bukkit version is not the recommended build for Essentials.");
			}
		} else {
			logger.log(Level.INFO, "Bukkit version format changed. Version not checked.");
		}
		

		playerListener = new EssentialsPlayerListener(this);
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Lowest, this);
		if (getSettings().getNetherPortalsEnabled())
			pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.High, this);
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

		jail = new Jail(this.getDataFolder());
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

		logger.info("Loaded " + this.getDescription().getName() + " build " + this.getDescription().getVersion() + " maintained by " + AUTHORS);
	}

	public void onDisable()
	{
		staticThis = null;
	}

	public void reload()
	{
		loadData();
		loadBanList();

		for (IConf iConf : confList)
		{
			iConf.reloadConfig();
		}

		try
		{
			ItemDb.load(getDataFolder(), "items.csv");
		}
		catch (Exception ex)
		{
			logger.log(Level.WARNING, "Could not load items.csv.", ex);
		}
	}

	public static Map<String, Object> getData(User player)
	{
		return getData(player.getName());
	}

	public static Map<String, Object> getData(String player)
	{
		try
		{
			Map<String, Object> retval;
			synchronized (usersLock)
			{
				retval = (Map<String, Object>)users.get(player.toLowerCase());
			}
			return retval == null ? new HashMap<String, Object>() : retval;
		}
		catch (Throwable ex)
		{
			return new HashMap<String, Object>();
		}
	}

	public static void flushData()
	{
		Thread run = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					if (!Essentials.getStatic().getDataFolder().exists())
						Essentials.getStatic().getDataFolder().mkdirs();
					File file = new File(Essentials.getStatic().getDataFolder(), "users.yml");
					if (!file.exists())
						file.createNewFile();

					FileWriter tx = new FileWriter(file);
					synchronized (usersLock)
					{
						tx.write(yaml.dump(users));
					}
					tx.flush();
					tx.close();
				}
				catch (Throwable ex)
				{
					Logger.getLogger(Essentials.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		run.setDaemon(false);
		run.start();
	}

	public static void loadData()
	{
		try
		{
			if (!Essentials.getStatic().getDataFolder().exists()) Essentials.getStatic().getDataFolder().mkdirs();
			File file = new File(Essentials.getStatic().getDataFolder(), "users.yml");
			if (!file.exists()) file.createNewFile();

			FileInputStream rx = new FileInputStream(file);
			synchronized (usersLock)
			{
				users = (Map<String, Object>)yaml.load(new UnicodeReader(rx));
			}
			rx.close();
		}
		catch (Exception ex)
		{
			Logger.getLogger(Essentials.class.getName()).log(Level.SEVERE, null, ex);
			synchronized (usersLock)
			{
				users = new HashMap<String, Object>();
			}
		}
		finally
		{
			synchronized (usersLock)
			{
				if (users == null) users = new HashMap<String, Object>();
			}
		}
	}

	public static void setData(User player, Map<String, Object> data)
	{
		setData(player.getName(), data);
	}

	public static void setData(String player, Map<String, Object> data)
	{
		synchronized (usersLock)
		{
			users.put(player.toLowerCase(), data);
		}
	}

	public static List<String> readMail(User player)
	{
		return readMail(player.getName());
	}

	public static List<String> readMail(String player)
	{
		try
		{
			Map<String, Object> data = getData(player);
			List<String> retval = (List<String>)data.get("mail");
			return retval == null ? new ArrayList<String>() : retval;
		}
		catch (Throwable ex)
		{
			return new ArrayList<String>();
		}
	}

	public static void clearMail(User player)
	{
		try
		{
			Map<String, Object> data = getData(player);
			data.put("mail", new ArrayList<String>());
			setData(player, data);
			flushData();
		}
		catch (Throwable ex)
		{
		}
	}

	public static void sendMail(User from, String to, String message)
			throws Exception
	{
		try
		{
			Map<String, Object> data = getData(ChatColor.stripColor(to));
			List<String> mail = readMail(to);
			mail.add(ChatColor.stripColor(from.getDisplayName()) + ": " + message);
			data.put("mail", mail);
			setData(to, data);
			flushData();
		}
		catch (Throwable ex)
		{
			throw new Exception("An error was encountered while sending the mail.", ex);
		}
	}

	public String readNickname(User player)
	{
		try
		{
			Map<String, Object> data = getData(player);
			String nick = (String)data.get("nickname");
			if (nick == null)
				return player.getName();
			if (nick.equals(player.getName()))
				return player.getName();
			return getSettings().getNicknamePrefix() + nick;
		}
		catch (Exception ex)
		{
			return player.getName();
		}
	}

	public void saveNickname(User player, String nickname) throws Exception
	{
		try
		{
			Map<String, Object> data = getData(player);
			data.put("nickname", nickname);
			setData(player, data);
			flushData();
		}
		catch (Throwable ex)
		{
			throw new Exception("An error was encountered while saving the nickname.", ex);
		}
	}

	public String[] getMotd(CommandSender sender, String def)
	{
		return getLines(sender, "motd", def);
	}

	public String[] getLines(CommandSender sender, String node, String def)
	{
		List<String> lines = (List<String>)getConfiguration().getProperty(node);
		if (lines == null) return new String[0];
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
				System.out.println(ChatColor.DARK_RED + "Notice: Your configuration file has a corrupt " + node + " node.");
				return new String[0];
			}
		}

		// if still empty, call it a day
		if (lines == null || lines.isEmpty() || lines.get(0) == null)
			return new String[0];

		for (int i = 0; i < lines.size(); i++)
		{
			String m = lines.get(i);
			if (m == null)
				continue;
			m = m.replace('&', '§').replace("§§", "&");

			if (sender instanceof User || sender instanceof Player)
			{
				User user = User.get(sender);
				m = m.replace("{PLAYER}", user.getDisplayName());
				m = m.replace("{IP}", user.getAddress().toString());
				m = m.replace("{BALANCE}", Double.toString(user.getMoney()));
			}

			m = m.replace("{ONLINE}", Integer.toString(getServer().getOnlinePlayers().length));

			if (m.matches(".*\\{PLAYERLIST\\}.*"))
			{
				StringBuilder online = new StringBuilder();
				for (Player p : getServer().getOnlinePlayers())
				{
					if (online.length() > 0)
						online.append(", ");
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

	public static String FormatTime(long Milliseconds)
	{	// format time into a string showing hours, minutes, or seconds
		if (Milliseconds > 3600000)
		{
			double val = Math.round((double)Milliseconds / 360000D) / 10D;
			return val + " hour" + (val > 1 ? "s" : "");
		}
		else if (Milliseconds > 60000)
		{
			double val = Math.round((double)Milliseconds / 6000D) / 10D;
			return val + " minute" + (val > 1 ? "s" : "");
		}
		else if (Milliseconds <= 1000)
			return "1 second";
		else
			return (Milliseconds / 1000L) + " seconds";
	}

	@SuppressWarnings("LoggerStringConcat")
	public static void previewCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if (sender instanceof Player)
			logger.info(ChatColor.BLUE + "[PLAYER_COMMAND] " + ((Player)sender).getName() + ": /" + commandLabel + " " + EssentialsCommand.getFinalArg(args, 0));
	}

	@Override
	@SuppressWarnings(
	{
		"LoggerStringConcat", "CallToThreadDumpStack"
	})
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		// Allow plugins to override the command via onCommand
		for (Plugin p : getServer().getPluginManager().getPlugins())
		{
			if (p == this)
				continue;

			PluginDescriptionFile desc = p.getDescription();
			if (desc == null)
				continue;

			if (desc.getName() == null)
				continue;

			if (!(desc.getCommands() instanceof Map))
				continue;

			Map<String, Object> cmds = (Map<String, Object>)desc.getCommands();
			if (!cmds.containsKey(command.getName()))
				continue;

			PluginCommand pcmd = getServer().getPluginCommand(desc.getName() + ":" + commandLabel);

			if (pcmd == null)
				continue;

			return getServer().getPluginCommand(p.getDescription().getName() + ":" + commandLabel).execute(sender, commandLabel, args);
		}

		try
		{
			previewCommand(sender, command, commandLabel, args);
			User user = sender instanceof Player ? User.get(sender) : null;

			// New mail notification
			if (user != null && !Essentials.getSettings().isCommandDisabled("mail") && !commandLabel.equals("mail"))
			{
				List<String> mail = Essentials.readMail(user);
				if (!mail.isEmpty()) user.sendMessage(ChatColor.RED + "You have " + mail.size() + " messages!§f Type §7/mail read§f to view your mail.");
			}

			// Check for disabled commands
			if (Essentials.getSettings().isCommandDisabled(commandLabel)) return true;

			IEssentialsCommand cmd;
			try
			{
				cmd = (IEssentialsCommand)Essentials.class.getClassLoader().loadClass("com.earth2me.essentials.commands.Command" + command.getName()).newInstance();
			}
			catch (Exception ex)
			{
				sender.sendMessage(ChatColor.RED + "That command is improperly loaded.");
				ex.printStackTrace();
				return true;
			}

			// Check authorization
			if (user != null && !user.isAuthorized(cmd))
			{
				logger.warning(user.getName() + " was denied access to command.");
				user.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}

			// Run the command
			try
			{
				if (user == null)
					cmd.run(getServer(), this, sender, commandLabel, command, args);
				else
					cmd.run(getServer(), this, user, commandLabel, command, args);
				return true;
			}
			catch (Throwable ex)
			{
				sender.sendMessage(ChatColor.RED + "Error: " + ex.getMessage());
				return true;
			}
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
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
			if (!file.exists()) throw new FileNotFoundException("banned-players.txt not found");

			BufferedReader rx = new BufferedReader(new FileReader(file));
			bans.clear();
			try
			{
				for (int i = 0; rx.ready(); i++)
				{

					String line = rx.readLine().trim().toLowerCase();
					if (line.startsWith("#")) continue;
					bans.add(line);

				}
			}
			catch (IOException io)
			{
				logger.log(Level.SEVERE, "Error reading banned-players.txt", io);
			}
		}
		catch (FileNotFoundException ex)
		{
			logger.log(Level.SEVERE, "Error reading banned-players.txt", ex);
		}

		try
		{
			if (!ipFile.exists()) throw new FileNotFoundException("banned-ips.txt not found");

			BufferedReader rx = new BufferedReader(new FileReader(ipFile));
			bannedIps.clear();
			try
			{
				for (int i = 0; rx.ready(); i++)
				{

					String line = rx.readLine().trim().toLowerCase();
					if (line.startsWith("#")) continue;
					bannedIps.add(line);

				}
			}
			catch (IOException io)
			{
				logger.log(Level.SEVERE, "Error reading banned-ips.txt", io);
			}
		}
		catch (FileNotFoundException ex)
		{
			logger.log(Level.SEVERE, "Error reading banned-ips.txt", ex);
		}
	}

	private void attachEcoListeners()
	{
		PluginManager pm = getServer().getPluginManager();
		EssentialsEcoBlockListener ecoBlockListener = new EssentialsEcoBlockListener();
		EssentialsEcoPlayerListener ecoPlayerListener = new EssentialsEcoPlayerListener();
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
}
