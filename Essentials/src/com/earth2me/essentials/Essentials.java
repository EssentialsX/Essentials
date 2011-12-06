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

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.IJails;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import com.earth2me.essentials.craftbukkit.ItemDupeFix;
import com.earth2me.essentials.perm.PermissionsHandler;
import com.earth2me.essentials.register.payment.Methods;
import com.earth2me.essentials.signs.SignBlockListener;
import com.earth2me.essentials.signs.SignEntityListener;
import com.earth2me.essentials.signs.SignPlayerListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.yaml.snakeyaml.error.YAMLException;


public class Essentials extends JavaPlugin implements IEssentials
{
	public static final int BUKKIT_VERSION = 1560;
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
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
		LOGGER.log(Level.INFO, _("usingTempFolderForTesting"));
		LOGGER.log(Level.INFO, dataFolder.toString());
		this.initialize(null, server, new PluginDescriptionFile(new FileReader(new File("src" + File.separator + "plugin.yml"))), dataFolder, null, null);
		settings = new Settings(this);
		i18n.updateLocale("en");
		userMap = new UserMap(this);
		permissionsHandler = new PermissionsHandler(this, false);
		Economy.setEss(this);
	}

	@Override
	public void onEnable()
	{
		execTimer = new ExecuteTimer();
		execTimer.start();
		i18n = new I18n(this);
		i18n.onEnable();
		execTimer.mark("I18n1");
		final PluginManager pm = getServer().getPluginManager();
		for (Plugin plugin : pm.getPlugins())
		{
			if (plugin.getDescription().getName().startsWith("Essentials")
				&& !plugin.getDescription().getVersion().equals(this.getDescription().getVersion()))
			{
				LOGGER.log(Level.WARNING, _("versionMismatch", plugin.getDescription().getName()));
			}
		}
		final Matcher versionMatch = Pattern.compile("git-Bukkit-([0-9]+).([0-9]+).([0-9]+)-R[0-9]+-[0-9]+-[0-9a-z]+-b([0-9]+)jnks.*").matcher(getServer().getVersion());
		if (versionMatch.matches())
		{
			final int versionNumber = Integer.parseInt(versionMatch.group(4));
			if (versionNumber < BUKKIT_VERSION)
			{
				LOGGER.log(Level.SEVERE, _("notRecommendedBukkit"));
				LOGGER.log(Level.SEVERE, _("requiredBukkit", Integer.toString(BUKKIT_VERSION)));
				this.setEnabled(false);
				return;
			}
		}
		else
		{
			LOGGER.log(Level.INFO, _("bukkitFormatChanged"));
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
			upgrade.afterSettings();
			execTimer.mark("Upgrade2");
			i18n.updateLocale(settings.getLocale());
			userMap = new UserMap(this);
			confList.add(userMap);
			execTimer.mark("Init(Usermap)");
			warps = new Warps(getServer(), this.getDataFolder());
			confList.add(warps);
			execTimer.mark("Init(Spawn/Warp)");
			worth = new Worth(this.getDataFolder());
			confList.add(worth);
			itemDb = new ItemDb(this);
			confList.add(itemDb);
			execTimer.mark("Init(Worth/ItemDB)");
			reload();
		}
		catch (YAMLException exception)
		{
			if (pm.getPlugin("EssentialsUpdate") != null)
			{
				LOGGER.log(Level.SEVERE, _("essentialsHelp2"));
			}
			else
			{
				LOGGER.log(Level.SEVERE, _("essentialsHelp1"));
			}
			LOGGER.log(Level.SEVERE, exception.toString());
			pm.registerEvent(Type.PLAYER_JOIN, new PlayerListener()
			{
				@Override
				public void onPlayerJoin(PlayerJoinEvent event)
				{
					event.getPlayer().sendMessage("Essentials failed to load, read the log file.");
				}
			}, Priority.Low, this);
			for (Player player : getServer().getOnlinePlayers())
			{
				player.sendMessage("Essentials failed to load, read the log file.");
			}
			this.setEnabled(false);
			return;
		}
		backup = new Backup(this);
		permissionsHandler = new PermissionsHandler(this, settings.useBukkitPermissions());
		alternativeCommandsHandler = new AlternativeCommandsHandler(this);
		final EssentialsPluginListener serverListener = new EssentialsPluginListener(this);
		pm.registerEvent(Type.PLUGIN_ENABLE, serverListener, Priority.Low, this);
		pm.registerEvent(Type.PLUGIN_DISABLE, serverListener, Priority.Low, this);
		confList.add(serverListener);

		final EssentialsPlayerListener playerListener = new EssentialsPlayerListener(this);
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_LOGIN, playerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_EGG_THROW, playerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_BUCKET_EMPTY, playerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_ANIMATION, playerListener, Priority.High, this);
		pm.registerEvent(Type.PLAYER_CHANGED_WORLD, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, new ItemDupeFix(), Priority.Monitor, this);

		final EssentialsBlockListener blockListener = new EssentialsBlockListener(this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Lowest, this);

		final SignBlockListener signBlockListener = new SignBlockListener(this);
		pm.registerEvent(Type.SIGN_CHANGE, signBlockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_PLACE, signBlockListener, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_BREAK, signBlockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_IGNITE, signBlockListener, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_BURN, signBlockListener, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_PISTON_EXTEND, signBlockListener, Priority.Low, this);
		pm.registerEvent(Type.BLOCK_PISTON_RETRACT, signBlockListener, Priority.Low, this);

		final SignPlayerListener signPlayerListener = new SignPlayerListener(this);
		pm.registerEvent(Type.PLAYER_INTERACT, signPlayerListener, Priority.Low, this);

		final SignEntityListener signEntityListener = new SignEntityListener(this);
		pm.registerEvent(Type.ENTITY_EXPLODE, signEntityListener, Priority.Low, this);
		pm.registerEvent(Type.ENDERMAN_PICKUP, signEntityListener, Priority.Low, this);

		final EssentialsEntityListener entityListener = new EssentialsEntityListener(this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_COMBUST, entityListener, Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_REGAIN_HEALTH, entityListener, Priority.Lowest, this);
		pm.registerEvent(Type.FOOD_LEVEL_CHANGE, entityListener, Priority.Lowest, this);

		//TODO: Check if this should be here, and not above before reload()
		jails = new Jails(this);
		confList.add(jails);

		pm.registerEvent(Type.ENTITY_EXPLODE, tntListener, Priority.High, this);

		final EssentialsTimer timer = new EssentialsTimer(this);
		getScheduler().scheduleSyncRepeatingTask(this, timer, 1, 100);
		Economy.setEss(this);
		execTimer.mark("RegListeners");
		LOGGER.info(_("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), Util.joinList(this.getDescription().getAuthors())));
		final String timeroutput = execTimer.end();
		if (getSettings().isDebug())
		{
			LOGGER.log(Level.INFO, "Essentials load " + timeroutput);
		}
	}

	@Override
	public void onDisable()
	{
		i18n.onDisable();
		Economy.setEss(null);
		Trade.closeLog();
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
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args)
	{
		return onCommandEssentials(sender, command, commandLabel, args, Essentials.class.getClassLoader(), "com.earth2me.essentials.commands.Command", "essentials.", null);
	}

	@Override
	public boolean onCommandEssentials(final CommandSender sender, final Command command, final String commandLabel, final String[] args, final ClassLoader classLoader, final String commandPath, final String permissionPrefix, final IEssentialsModule module)
	{
		// Allow plugins to override the command via onCommand
		if (!getSettings().isCommandOverridden(command.getName()) && !commandLabel.startsWith("e"))
		{
			final PluginCommand pc = alternativeCommandsHandler.getAlternative(commandLabel);
			if (pc != null)
			{
				alternativeCommandsHandler.executed(commandLabel, pc.getLabel());				
				return pc.execute(sender, commandLabel, args);
			}
		}

		try
		{
			User user = null;
			if (sender instanceof Player)
			{
				user = getUser(sender);
				LOGGER.log(Level.INFO, String.format("[PLAYER_COMMAND] %s: /%s %s ", ((Player)sender).getName(), commandLabel, EssentialsCommand.getFinalArg(args, 0)));
			}

			// New mail notification
			if (user != null && !getSettings().isCommandDisabled("mail") && !commandLabel.equals("mail") && user.isAuthorized("essentials.mail"))
			{
				final List<String> mail = user.getMails();
				if (mail != null && !mail.isEmpty())
				{
					user.sendMessage(_("youHaveNewMail", mail.size()));
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
				cmd.setEssentialsModule(module);
			}
			catch (Exception ex)
			{
				sender.sendMessage(_("commandNotLoaded", commandLabel));
				LOGGER.log(Level.SEVERE, _("commandNotLoaded", commandLabel), ex);
				return true;
			}

			// Check authorization
			if (user != null && !user.isAuthorized(cmd, permissionPrefix))
			{
				LOGGER.log(Level.WARNING, _("deniedAccessCommand", user.getName()));
				user.sendMessage(_("noAccessCommand"));
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
			catch (Throwable ex)
			{
				showError(sender, ex, commandLabel);
				return true;
			}
		}
		catch (Throwable ex)
		{
			LOGGER.log(Level.SEVERE, _("commandFailed", commandLabel), ex);
			return true;
		}
	}

	@Override
	public void showError(final CommandSender sender, final Throwable exception, final String commandLabel)
	{
		sender.sendMessage(_("errorWithMessage", exception.getMessage()));
		if (getSettings().isDebug())
		{
			LOGGER.log(Level.WARNING, _("errorCallingCommand", commandLabel), exception);
		}
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
	public User getUser(final Object base)
	{
		if (base instanceof Player)
		{
			return getUser((Player)base);
		}
		if (base instanceof String)
		{
			return userMap.getUser((String)base);
		}
		return null;
	}

	private <T extends Player> User getUser(final T base)
	{
		if (base == null)
		{
			return null;
		}

		if (base instanceof User)
		{
			return (User)base;
		}
		User user = userMap.getUser(base.getName());

		if (user == null)
		{
			user = new User(base, this);
		}
		else
		{
			user.update(base);
		}
		return user;
	}

	@Override
	public User getOfflineUser(final String name)
	{
		return userMap.getUser(name);
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
	public int broadcastMessage(final IUser sender, final String message)
	{
		if (sender == null)
		{
			return getServer().broadcastMessage(message);
		}
		if (sender.isHidden())
		{
			return 0;
		}
		final Player[] players = getServer().getOnlinePlayers();

		for (Player player : players)
		{
			final User user = getUser(player);
			if (!user.isIgnoredPlayer(sender.getName()))
			{
				player.sendMessage(message);
			}
		}

		return players.length;
	}

	@Override
	public int scheduleAsyncDelayedTask(final Runnable run)
	{
		return this.getScheduler().scheduleAsyncDelayedTask(this, run);
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
	public ItemDb getItemDb()
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
}
