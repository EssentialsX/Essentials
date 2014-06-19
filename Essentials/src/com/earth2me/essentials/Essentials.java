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

import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import com.earth2me.essentials.commands.QuietAbortException;
import com.earth2me.essentials.metrics.Metrics;
import com.earth2me.essentials.metrics.MetricsListener;
import com.earth2me.essentials.metrics.MetricsStarter;
import com.earth2me.essentials.perm.PermissionsHandler;
import com.earth2me.essentials.register.payment.Methods;
import com.earth2me.essentials.signs.SignBlockListener;
import com.earth2me.essentials.signs.SignEntityListener;
import com.earth2me.essentials.signs.SignPlayerListener;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.utils.DateUtil;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.ess3.api.Economy;
import net.ess3.api.IEssentials;
import net.ess3.api.IItemDb;
import net.ess3.api.IJails;
import net.ess3.api.ISettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.yaml.snakeyaml.error.YAMLException;


public class Essentials extends JavaPlugin implements net.ess3.api.IEssentials
{
	public static final int BUKKIT_VERSION = 3050;
	private static final Logger LOGGER = Logger.getLogger("Essentials");
	private transient ISettings settings;
	private final transient TNTExplodeListener tntListener = new TNTExplodeListener(this);
	private transient Jails jails;
	private transient Warps warps;
	private transient Worth worth;
	private transient List<IConf> confList;
	private transient Backup backup;
	private transient ItemDb itemDb;
	private transient final Methods paymentMethod = new Methods();
	private transient PermissionsHandler permissionsHandler;
	private transient AlternativeCommandsHandler alternativeCommandsHandler;
	private transient UserMap userMap;
	private transient ExecuteTimer execTimer;
	private transient I18n i18n;
	private transient Metrics metrics;
	private transient EssentialsTimer timer;
	private final transient List<String> vanishedPlayers = new ArrayList<String>();

	public Essentials()
	{
	}

	public Essentials(final Server server)
	{
		super(new JavaPluginLoader(server), new PluginDescriptionFile("Essentials", "", "com.earth2me.essentials.Essentials"), null, null);
	}

	@Override
	public ISettings getSettings()
	{
		return settings;
	}

	public void setupForTesting(final Server server) throws IOException, InvalidDescriptionException
	{
		final File dataFolder = File.createTempFile("essentialstest", "");
		if (!dataFolder.delete())
		{
			throw new IOException();
		}
		if (!dataFolder.mkdir())
		{
			throw new IOException();
		}
		i18n = new I18n(this);
		i18n.onEnable();
		i18n.updateLocale("en");
		LOGGER.log(Level.INFO, tl("usingTempFolderForTesting"));
		LOGGER.log(Level.INFO, dataFolder.toString());
		this.initialize(null, server, new PluginDescriptionFile(new FileReader(new File("src" + File.separator + "plugin.yml"))), dataFolder, null, null);
		settings = new Settings(this);
		userMap = new UserMap(this);
		permissionsHandler = new PermissionsHandler(this, false);
		Economy.setEss(this);
		confList = new ArrayList<IConf>();
		jails = new Jails(this);
		registerListeners(server.getPluginManager());
	}

	@Override
	public void onEnable()
	{
		try
		{
			LOGGER.setParent(this.getLogger());
			execTimer = new ExecuteTimer();
			execTimer.start();
			i18n = new I18n(this);
			i18n.onEnable();
			execTimer.mark("I18n1");
			final PluginManager pm = getServer().getPluginManager();
			for (Plugin plugin : pm.getPlugins())
			{
				if (plugin.getDescription().getName().startsWith("Essentials")
					&& !plugin.getDescription().getVersion().equals(this.getDescription().getVersion())
					&& !plugin.getDescription().getName().equals("EssentialsAntiCheat"))
				{
					LOGGER.log(Level.WARNING, tl("versionMismatch", plugin.getDescription().getName()));
				}
			}
			final Matcher versionMatch = Pattern.compile("git-Bukkit-(?:(?:[0-9]+)\\.)+[0-9]+-R[\\.0-9]+-(?:[0-9]+-g[0-9a-f]+-)?b([0-9]+)jnks.*").matcher(getServer().getVersion());
			if (versionMatch.matches())
			{
				final int versionNumber = Integer.parseInt(versionMatch.group(1));
				if (versionNumber < BUKKIT_VERSION && versionNumber > 100)
				{
					wrongVersion();
					this.setEnabled(false);
					return;
				}
			}
			else
			{
				LOGGER.log(Level.INFO, tl("bukkitFormatChanged"));
				LOGGER.log(Level.INFO, getServer().getVersion());
				LOGGER.log(Level.INFO, getServer().getBukkitVersion());
			}
			execTimer.mark("BukkitCheck");
			try
			{
				final EssentialsUpgrade upgrade = new EssentialsUpgrade(this);
				upgrade.beforeSettings();
				execTimer.mark("Upgrade");
				confList = new ArrayList<IConf>();
				settings = new Settings(this);
				confList.add(settings);
				execTimer.mark("Settings");
				userMap = new UserMap(this);
				confList.add(userMap);
				execTimer.mark("Init(Usermap)");
				upgrade.afterSettings();
				execTimer.mark("Upgrade2");
				i18n.updateLocale(settings.getLocale());
				warps = new Warps(getServer(), this.getDataFolder());
				confList.add(warps);
				execTimer.mark("Init(Spawn/Warp)");
				worth = new Worth(this.getDataFolder());
				confList.add(worth);
				itemDb = new ItemDb(this);
				confList.add(itemDb);
				execTimer.mark("Init(Worth/ItemDB)");
				jails = new Jails(this);
				confList.add(jails);
				reload();
			}
			catch (YAMLException exception)
			{
				if (pm.getPlugin("EssentialsUpdate") != null)
				{
					LOGGER.log(Level.SEVERE, tl("essentialsHelp2"));
				}
				else
				{
					LOGGER.log(Level.SEVERE, tl("essentialsHelp1"));
				}
				handleCrash(exception);
				return;
			}
			backup = new Backup(this);
			permissionsHandler = new PermissionsHandler(this, settings.useBukkitPermissions());
			alternativeCommandsHandler = new AlternativeCommandsHandler(this);

			timer = new EssentialsTimer(this);
			scheduleSyncRepeatingTask(timer, 1000, 50);

			Economy.setEss(this);
			execTimer.mark("RegHandler");

			final MetricsStarter metricsStarter = new MetricsStarter(this);
			if (metricsStarter.getStart() != null && metricsStarter.getStart() == true)
			{
				runTaskLaterAsynchronously(metricsStarter, 1);
			}
			else if (metricsStarter.getStart() != null && metricsStarter.getStart() == false)
			{
				final MetricsListener metricsListener = new MetricsListener(this, metricsStarter);
				pm.registerEvents(metricsListener, this);
			}

			final String timeroutput = execTimer.end();
			if (getSettings().isDebug())
			{
				LOGGER.log(Level.INFO, "Essentials load {0}", timeroutput);
			}
		}
		catch (NumberFormatException ex)
		{
			handleCrash(ex);
		}
		catch (Error ex)
		{
			handleCrash(ex);
			throw ex;
		}
	}

	@Override
	public void saveConfig()
	{
		// We don't use any of the bukkit config writing, as this breaks our config file formatting.
	}

	private void registerListeners(PluginManager pm)
	{
		HandlerList.unregisterAll(this);

		if (getSettings().isDebug())
		{
			LOGGER.log(Level.INFO, "Registering Listeners");
		}

		final EssentialsPluginListener serverListener = new EssentialsPluginListener(this);
		pm.registerEvents(serverListener, this);
		confList.add(serverListener);

		final EssentialsPlayerListener playerListener = new EssentialsPlayerListener(this);
		pm.registerEvents(playerListener, this);

		final EssentialsBlockListener blockListener = new EssentialsBlockListener(this);
		pm.registerEvents(blockListener, this);

		final SignBlockListener signBlockListener = new SignBlockListener(this);
		pm.registerEvents(signBlockListener, this);

		final SignPlayerListener signPlayerListener = new SignPlayerListener(this);
		pm.registerEvents(signPlayerListener, this);

		final SignEntityListener signEntityListener = new SignEntityListener(this);
		pm.registerEvents(signEntityListener, this);

		final EssentialsEntityListener entityListener = new EssentialsEntityListener(this);
		pm.registerEvents(entityListener, this);

		final EssentialsWorldListener worldListener = new EssentialsWorldListener(this);
		pm.registerEvents(worldListener, this);

		pm.registerEvents(tntListener, this);

		jails.resetListener();
	}

	@Override
	public void onDisable()
	{
		for (Player p : getServer().getOnlinePlayers())
		{
			User user = getUser(p);
			if (user.isVanished())
			{
				user.setVanished(false);
				user.sendMessage(tl("unvanishedReload"));
			}
			user.stopTransaction();
		}
		cleanupOpenInventories();
		if (i18n != null)
		{
			i18n.onDisable();
		}
		if (backup != null)
		{
			backup.stopTask();
		}
		Economy.setEss(null);
		Trade.closeLog();
		getUserMap().getUUIDMap().forceWriteUUIDMap();
		
		HandlerList.unregisterAll(this);
	}

	@Override
	public void reload()
	{
		Trade.closeLog();

		for (IConf iConf : confList)
		{
			iConf.reloadConfig();
			execTimer.mark("Reload(" + iConf.getClass().getSimpleName() + ")");
		}

		i18n.updateLocale(settings.getLocale());

		final PluginManager pm = getServer().getPluginManager();
		registerListeners(pm);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender,
									  Command command,
									  String commandLabel,
									  String[] args)
	{
		// Allow plugins to override the command via onCommand
		if (!getSettings().isCommandOverridden(command.getName()) && (!commandLabel.startsWith("e") || commandLabel.equalsIgnoreCase(command.getName())))
		{
			final PluginCommand pc = alternativeCommandsHandler.getAlternative(commandLabel);
			if (pc != null)
			{
				try
				{
					TabCompleter completer = pc.getTabCompleter();
					if (completer != null)
					{
						return completer.onTabComplete(sender, command, commandLabel, args);
					}
				}
				catch (final Exception ex)
				{
					Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		}
		return null;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args)
	{
		return onCommandEssentials(sender, command, commandLabel, args, Essentials.class.getClassLoader(), "com.earth2me.essentials.commands.Command", "essentials.", null);
	}

	@Override
	public boolean onCommandEssentials(final CommandSender cSender, final Command command, final String commandLabel, final String[] args, final ClassLoader classLoader, final String commandPath, final String permissionPrefix, final IEssentialsModule module)
	{
		// Allow plugins to override the command via onCommand
		if (!getSettings().isCommandOverridden(command.getName()) && (!commandLabel.startsWith("e") || commandLabel.equalsIgnoreCase(command.getName())))
		{
			final PluginCommand pc = alternativeCommandsHandler.getAlternative(commandLabel);
			if (pc != null)
			{
				alternativeCommandsHandler.executed(commandLabel, pc);
				try
				{
					return pc.execute(cSender, commandLabel, args);
				}
				catch (final Exception ex)
				{
					Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
					cSender.sendMessage(ChatColor.RED + "An internal error occurred while attempting to perform this command");
					return true;
				}
			}
		}

		try
		{

			User user = null;
			Block bSenderBlock = null;
			if (cSender instanceof Player)
			{
				user = getUser((Player)cSender);
			}
			else if (cSender instanceof BlockCommandSender)
			{
				BlockCommandSender bsender = (BlockCommandSender)cSender;
				bSenderBlock = bsender.getBlock();
			}

			if (bSenderBlock != null)
			{
				Bukkit.getLogger().log(Level.INFO, "CommandBlock at {0},{1},{2} issued server command: /{3} {4}", new Object[]
				{
					bSenderBlock.getX(), bSenderBlock.getY(), bSenderBlock.getZ(), commandLabel, EssentialsCommand.getFinalArg(args, 0)
				});
			}
			else if (user == null)
			{
				Bukkit.getLogger().log(Level.INFO, "{0} issued server command: /{1} {2}", new Object[]
				{
					cSender.getName(), commandLabel, EssentialsCommand.getFinalArg(args, 0)
				});
			}

			CommandSource sender = new CommandSource(cSender);

			// New mail notification
			if (user != null && !getSettings().isCommandDisabled("mail") && !command.getName().equals("mail") && user.isAuthorized("essentials.mail"))
			{
				final List<String> mail = user.getMails();
				if (mail != null && !mail.isEmpty())
				{
					user.sendMessage(tl("youHaveNewMail", mail.size()));
				}
			}

			//Print version even if admin command is not available #easteregg
			if (commandLabel.equalsIgnoreCase("essversion"))
			{
				sender.sendMessage("This server is running Essentials " + getDescription().getVersion());
				return true;
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
				cmd.setEssentialsModule(module);
			}
			catch (Exception ex)
			{
				sender.sendMessage(tl("commandNotLoaded", commandLabel));
				LOGGER.log(Level.SEVERE, tl("commandNotLoaded", commandLabel), ex);
				return true;
			}

			// Check authorization
			if (user != null && !user.isAuthorized(cmd, permissionPrefix))
			{
				LOGGER.log(Level.INFO, tl("deniedAccessCommand", user.getName()));
				user.sendMessage(tl("noAccessCommand"));
				return true;
			}

			if (user != null && user.isJailed() && !user.isAuthorized(cmd, "essentials.jail.allow."))
			{
				if (user.getJailTimeout() > 0)
				{
					user.sendMessage(tl("playerJailedFor", user.getName(), DateUtil.formatDateDiff(user.getJailTimeout())));
				}
				else
				{
					user.sendMessage(tl("jailMessage"));
				}
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
			catch (NoChargeException ex)
			{
				return true;
			}
			catch (QuietAbortException ex)
			{
				return true;
			}
			catch (NotEnoughArgumentsException ex)
			{
				sender.sendMessage(command.getDescription());
				sender.sendMessage(command.getUsage().replaceAll("<command>", commandLabel));
				if (!ex.getMessage().isEmpty())
				{
					sender.sendMessage(ex.getMessage());
				}
				return true;
			}
			catch (Exception ex)
			{
				showError(sender, ex, commandLabel);
				return true;
			}
		}
		catch (Throwable ex)
		{
			LOGGER.log(Level.SEVERE, tl("commandFailed", commandLabel), ex);
			return true;
		}
	}

	public void cleanupOpenInventories()
	{
		for (Player player : getServer().getOnlinePlayers())
		{
			User user = getUser(player);
			if (user.isRecipeSee())
			{
				user.getBase().getOpenInventory().getTopInventory().clear();
				user.getBase().getOpenInventory().close();
				user.setRecipeSee(false);
			}
			if (user.isInvSee() || user.isEnderSee())
			{
				user.getBase().getOpenInventory().close();
				user.setInvSee(false);
				user.setEnderSee(false);
			}
		}
	}

	@Override
	public void showError(final CommandSource sender, final Throwable exception, final String commandLabel)
	{
		sender.sendMessage(tl("errorWithMessage", exception.getMessage()));
		if (getSettings().isDebug())
		{
			LOGGER.log(Level.INFO, tl("errorCallingCommand", commandLabel), exception);
		}
	}

	public static void wrongVersion()
	{
		LOGGER.log(Level.SEVERE, " * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! *");
		LOGGER.log(Level.SEVERE, tl("notRecommendedBukkit"));
		LOGGER.log(Level.SEVERE, tl("requiredBukkit", Integer.toString(BUKKIT_VERSION)));
		LOGGER.log(Level.SEVERE, " * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! *");
	}

	@Override
	public BukkitScheduler getScheduler()
	{
		return this.getServer().getScheduler();
	}

	@Override
	public IJails getJails()
	{
		return jails;
	}

	@Override
	public Warps getWarps()
	{
		return warps;
	}

	@Override
	public Worth getWorth()
	{
		return worth;
	}

	@Override
	public Backup getBackup()
	{
		return backup;
	}

	@Override
	public Metrics getMetrics()
	{
		return metrics;
	}

	@Override
	public void setMetrics(Metrics metrics)
	{
		this.metrics = metrics;
	}

	@Deprecated
	@Override
	public User getUser(final Object base)
	{
		if (base instanceof Player)
		{
			return getUser((Player)base);
		}
		if (base instanceof org.bukkit.OfflinePlayer)
		{
			return getUser(((org.bukkit.OfflinePlayer)base).getUniqueId());
		}
		if (base instanceof UUID)
		{
			return getUser((UUID)base);
		}
		if (base instanceof String)
		{
			return getOfflineUser((String)base);
		}
		return null;
	}

	//This will return null if there is not a match.
	@Override
	public User getUser(final String base)
	{
		return getOfflineUser(base);
	}

	//This will return null if there is not a match.
	@Override
	public User getUser(final UUID base)
	{
		return userMap.getUser(base);
	}

	//This will return null if there is not a match.
	@Override
	public User getOfflineUser(final String name)
	{
		final User user = userMap.getUser(name);
		if (user != null && user.getBase() instanceof OfflinePlayer)
		{
			//This code should attempt to use the last known name of a user, if Bukkit returns name as null.
			final String lastName = user.getLastAccountName();
			if (lastName != null)
			{
				((OfflinePlayer)user.getBase()).setName(lastName);
			}
			else
			{
				((OfflinePlayer)user.getBase()).setName(name);
			}
		}
		return user;
	}

	//This will create a new user if there is not a match.
	@Override
	public User getUser(final Player base)
	{
		if (base == null)
		{
			return null;
		}

		if (userMap == null)
		{
			LOGGER.log(Level.WARNING, "Essentials userMap not initialized");
			return null;
		}

		User user = userMap.getUser(base.getUniqueId());

		if (user == null)
		{
			if (getSettings().isDebug())
			{
				LOGGER.log(Level.INFO, "Constructing new userfile from base player {0}", base.getName());
			}
			user = new User(base, this);
		}
		else
		{
			user.update(base);
		}
		return user;
	}

	private void handleCrash(Throwable exception)
	{
		final PluginManager pm = getServer().getPluginManager();
		LOGGER.log(Level.SEVERE, exception.toString());
		pm.registerEvents(new Listener()
		{
			@EventHandler(priority = EventPriority.LOW)
			public void onPlayerJoin(final PlayerJoinEvent event)
			{
				event.getPlayer().sendMessage("Essentials failed to load, read the log file.");
			}
		}, this);
		for (Player player : getServer().getOnlinePlayers())
		{
			player.sendMessage("Essentials failed to load, read the log file.");
		}
		this.setEnabled(false);
	}

	@Override
	public World getWorld(final String name)
	{
		if (name.matches("[0-9]+"))
		{
			final int worldId = Integer.parseInt(name);
			if (worldId < getServer().getWorlds().size())
			{
				return getServer().getWorlds().get(worldId);
			}
		}
		return getServer().getWorld(name);
	}

	@Override
	public void addReloadListener(final IConf listener)
	{
		confList.add(listener);
	}

	@Override
	public Methods getPaymentMethod()
	{
		return paymentMethod;
	}

	@Override
	public int broadcastMessage(final String message)
	{
		return broadcastMessage(null, null, message, true);
	}

	@Override
	public int broadcastMessage(final IUser sender, final String message)
	{
		return broadcastMessage(sender, null, message, false);
	}

	@Override
	public int broadcastMessage(final String permission, final String message)
	{
		return broadcastMessage(null, permission, message, false);
	}

	private int broadcastMessage(final IUser sender, final String permission, final String message, final boolean keywords)
	{
		if (sender != null && sender.isHidden())
		{
			return 0;
		}

		IText broadcast = new SimpleTextInput(message);

		final Player[] players = getServer().getOnlinePlayers();

		for (Player player : players)
		{
			final User user = getUser(player);
			if ((permission == null && (sender == null || !user.isIgnoredPlayer(sender)))
				|| (permission != null && user.isAuthorized(permission)))
			{
				if (keywords)
				{
					broadcast = new KeywordReplacer(broadcast, new CommandSource(player), this, false);
				}
				for (String messageText : broadcast.getLines())
				{
					user.sendMessage(messageText);
				}
			}
		}

		return players.length;
	}

	@Override
	public BukkitTask runTaskAsynchronously(final Runnable run)
	{
		return this.getScheduler().runTaskAsynchronously(this, run);
	}

	@Override
	public BukkitTask runTaskLaterAsynchronously(final Runnable run, final long delay)
	{
		return this.getScheduler().runTaskLaterAsynchronously(this, run, delay);
	}
	
	@Override
	public BukkitTask runTaskTimerAsynchronously(final Runnable run, final long delay, final long period)
	{
		return this.getScheduler().runTaskTimerAsynchronously(this, run, delay, period);
	}

	@Override
	public int scheduleSyncDelayedTask(final Runnable run)
	{
		return this.getScheduler().scheduleSyncDelayedTask(this, run);
	}

	@Override
	public int scheduleSyncDelayedTask(final Runnable run, final long delay)
	{
		return this.getScheduler().scheduleSyncDelayedTask(this, run, delay);
	}

	@Override
	public int scheduleSyncRepeatingTask(final Runnable run, final long delay, final long period)
	{
		return this.getScheduler().scheduleSyncRepeatingTask(this, run, delay, period);
	}

	@Override
	public TNTExplodeListener getTNTListener()
	{
		return tntListener;
	}

	@Override
	public PermissionsHandler getPermissionsHandler()
	{
		return permissionsHandler;
	}

	@Override
	public AlternativeCommandsHandler getAlternativeCommandsHandler()
	{
		return alternativeCommandsHandler;
	}

	@Override
	public IItemDb getItemDb()
	{
		return itemDb;
	}

	@Override
	public UserMap getUserMap()
	{
		return userMap;
	}

	@Override
	public I18n getI18n()
	{
		return i18n;
	}

	@Override
	public EssentialsTimer getTimer()
	{
		return timer;
	}

	@Override
	public List<String> getVanishedPlayers()
	{
		return vanishedPlayers;
	}


	private static class EssentialsWorldListener implements Listener, Runnable
	{
		private transient final IEssentials ess;

		public EssentialsWorldListener(final IEssentials ess)
		{
			this.ess = ess;
		}

		@EventHandler(priority = EventPriority.LOW)
		public void onWorldLoad(final WorldLoadEvent event)
		{
			ess.getJails().onReload();
			ess.getWarps().reloadConfig();
			for (IConf iConf : ((Essentials)ess).confList)
			{
				if (iConf instanceof IEssentialsModule)
				{
					iConf.reloadConfig();
				}
			}
		}

		@EventHandler(priority = EventPriority.LOW)
		public void onWorldUnload(final WorldUnloadEvent event)
		{
			ess.getJails().onReload();
			ess.getWarps().reloadConfig();
			for (IConf iConf : ((Essentials)ess).confList)
			{
				if (iConf instanceof IEssentialsModule)
				{
					iConf.reloadConfig();
				}
			}
		}

		@Override
		public void run()
		{
			ess.reload();
		}
	}
}
