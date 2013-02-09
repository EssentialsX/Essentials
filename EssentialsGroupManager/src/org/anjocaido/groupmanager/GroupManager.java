/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager;

import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.anjocaido.groupmanager.permissions.BukkitPermissions;
import org.anjocaido.groupmanager.utils.GroupManagerPermissions;
import org.anjocaido.groupmanager.Tasks.BukkitPermsUpdateTask;
import org.anjocaido.groupmanager.data.Variables;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.anjocaido.groupmanager.events.GMSystemEvent;
import org.anjocaido.groupmanager.events.GMWorldListener;
import org.anjocaido.groupmanager.events.GroupManagerEventHandler;
import org.anjocaido.groupmanager.utils.GMLoggerHandler;
import org.anjocaido.groupmanager.utils.PermissionCheckResult;
import org.anjocaido.groupmanager.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;


/**
 *
 * @author gabrielcouto, ElgarL
 */
public class GroupManager extends JavaPlugin {

	private File backupFolder;
	private Runnable commiter;
	private ScheduledThreadPoolExecutor scheduler;
	private Map<String, ArrayList<User>> overloadedUsers = new HashMap<String, ArrayList<User>>();
	private Map<CommandSender, String> selectedWorlds = new HashMap<CommandSender, String>();
	private WorldsHolder worldsHolder;
	private boolean validateOnlinePlayer = true;
	
	private static boolean isLoaded = false;
	protected GMConfiguration config;

	protected static GlobalGroups globalGroups;

	private GMLoggerHandler ch;
	
	private static GroupManagerEventHandler GMEventHandler;
	public static BukkitPermissions BukkitPermissions;
	private static GMWorldListener WorldEvents;
	public static final Logger logger = Logger.getLogger(GroupManager.class.getName());

	// PERMISSIONS FOR COMMAND BEING LOADED
	private OverloadedWorldHolder dataHolder = null;
	private AnjoPermissionsHandler permissionHandler = null;

	private String lastError = "";

	@Override
	public void onDisable() {
		
		onDisable(false);
	}
	
	@Override
	public void onEnable() {
		/*
		 * Initialize the event handler
		 */
		setGMEventHandler(new GroupManagerEventHandler(this));
		onEnable(false);
	}
	
	public void onDisable(boolean restarting) {

		setLoaded(false);

		if (!restarting) {
			// Unregister this service if we are shutting down.
			this.getServer().getServicesManager().unregister(this.worldsHolder);
		}

		disableScheduler(); // Shutdown before we save, so it doesn't interfere.
		if (worldsHolder != null) {
			try {
				worldsHolder.saveChanges(false);
			} catch (IllegalStateException ex) {
				GroupManager.logger.log(Level.WARNING, ex.getMessage());
			}
		}

		

		// Remove all attachments before clearing
		if (BukkitPermissions != null) {
			BukkitPermissions.removeAllAttachments();
		}
		
		if (!restarting) {
			
			if (WorldEvents != null)
				WorldEvents = null;

			BukkitPermissions = null;
			
		}

		// EXAMPLE: Custom code, here we just output some info so we can check that all is well
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
		
		if (!restarting)
			GroupManager.logger.removeHandler(ch);
	}
	
	public void onEnable(boolean restarting) {

		try {
			/*
			 * reset local variables.
			 */
			overloadedUsers = new HashMap<String, ArrayList<User>>();
			selectedWorlds = new HashMap<CommandSender, String>();
			lastError = "";
			
			/*
			 * Setup our logger if we are not restarting.
			 */
			if (!restarting) {
				GroupManager.logger.setUseParentHandlers(false);
				ch = new GMLoggerHandler();
				GroupManager.logger.addHandler(ch);
			}
			GroupManager.logger.setLevel(Level.ALL);

			// Create the backup folder, if it doesn't exist.
			prepareFileFields();
			// Load the config.yml
			prepareConfig();
			// Load the global groups
			globalGroups = new GlobalGroups(this);
			
			/*
			 * Configure the worlds holder.
			 */
			if (!restarting)
				worldsHolder = new WorldsHolder(this);
			else
				worldsHolder.resetWorldsHolder();

			/*
			 * This should NEVER happen. No idea why it's still here.
			 */
			PluginDescriptionFile pdfFile = this.getDescription();
			if (worldsHolder == null) {
				GroupManager.logger.severe("Can't enable " + pdfFile.getName() + " version " + pdfFile.getVersion() + ", bad loading!");
				this.getServer().getPluginManager().disablePlugin(this);
				throw new IllegalStateException("An error ocurred while loading GroupManager");
			}

			/*
			 *  Prevent our registered events from triggering
			 *  updates as we are not fully loaded.
			 */
			setLoaded(false);

			/*
			 *  Initialize the world listener and bukkit permissions
			 *  to handle events if this is a fresh start
			 *  
			 *  else
			 *  
			 *  Reset bukkit perms.
			 */
			if (!restarting) {
				WorldEvents = new GMWorldListener(this);
				BukkitPermissions = new BukkitPermissions(this);
			} else {
				BukkitPermissions.reset();
			}

			/*
			 * Start the scheduler for data saving.
			 */
			enableScheduler();

			/*
			 * Schedule a Bukkit Permissions update for 1 tick later.
			 * All plugins will be loaded by then
			 */

			if (getServer().getScheduler().scheduleSyncDelayedTask(this, new BukkitPermsUpdateTask(), 1) == -1) {
				GroupManager.logger.severe("Could not schedule superperms Update.");
				/*
				 * Flag that we are now loaded and should start processing events.
				 */
				setLoaded(true);
			}

			System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");

			// Register as a service
			if (!restarting)
				this.getServer().getServicesManager().register(WorldsHolder.class, this.worldsHolder, this, ServicePriority.Lowest);
			
		} catch (Exception ex) {

			/*
			 * Store the error and write to the log.
			 */
			saveErrorLog(ex);

			/*
			 * Throw an error so Bukkit knows about it.
			 */
			throw new IllegalArgumentException(ex.getMessage(), ex);

		}
	}

	/**
	 * Write an error.log
	 * 
	 * @param ex
	 */
	private void saveErrorLog(Exception ex) {

		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}

		lastError = ex.getMessage();

		GroupManager.logger.severe("===================================================");
		GroupManager.logger.severe("= ERROR REPORT START - " + this.getDescription().getVersion() + " =");
		GroupManager.logger.severe("===================================================");
		GroupManager.logger.severe("=== PLEASE COPY AND PASTE THE ERROR.LOG FROM THE ==");
		GroupManager.logger.severe("= GROUPMANAGER FOLDER TO AN ESSENTIALS  DEVELOPER =");
		GroupManager.logger.severe("===================================================");
		GroupManager.logger.severe(lastError);
		GroupManager.logger.severe("===================================================");
		GroupManager.logger.severe("= ERROR REPORT ENDED =");
		GroupManager.logger.severe("===================================================");

		// Append this error to the error log.
		try {
			String error = "=============================== GM ERROR LOG ===============================\n";
			error += "= ERROR REPORT START - " + this.getDescription().getVersion() + " =\n\n";
			
			error += Tasks.getStackTraceAsString(ex);
			error += "\n============================================================================\n";

			Tasks.appendStringToFile(error, (getDataFolder() + System.getProperty("file.separator") + "ERROR.LOG"));
		} catch (IOException e) {
			// Failed to write file.
			e.printStackTrace();
		}

	}
	
	/**
	 * @return the validateOnlinePlayer
	 */
	public boolean isValidateOnlinePlayer() {

		return validateOnlinePlayer;
	}

	/**
	 * @param validateOnlinePlayer the validateOnlinePlayer to set
	 */
	public void setValidateOnlinePlayer(boolean validateOnlinePlayer) {

		this.validateOnlinePlayer = validateOnlinePlayer;
	}

	public static boolean isLoaded() {

		return isLoaded;
	}

	public static void setLoaded(boolean isLoaded) {

		GroupManager.isLoaded = isLoaded;
	}

	public InputStream getResourceAsStream(String fileName) {

		return this.getClassLoader().getResourceAsStream(fileName);
	}

	private void prepareFileFields() {

		backupFolder = new File(this.getDataFolder(), "backup");
		if (!backupFolder.exists()) {
			getBackupFolder().mkdirs();
		}
	}

	private void prepareConfig() {

		config = new GMConfiguration(this);
	}

	public void enableScheduler() {

		if (worldsHolder != null) {
			disableScheduler();
			commiter = new Runnable() {

				@Override
				public void run() {

					try {
						if (worldsHolder.saveChanges(false))
							GroupManager.logger.log(Level.INFO, " Data files refreshed.");
					} catch (IllegalStateException ex) {
						GroupManager.logger.log(Level.WARNING, ex.getMessage());
					}
				}
			};
			scheduler = new ScheduledThreadPoolExecutor(1);
			long minutes = (long) getGMConfig().getSaveInterval();
			if (minutes > 0) {
				scheduler.scheduleAtFixedRate(commiter, minutes, minutes, TimeUnit.MINUTES);
				GroupManager.logger.info("Scheduled Data Saving is set for every " + minutes + " minutes!");
			} else
				GroupManager.logger.info("Scheduled Data Saving is Disabled!");

			GroupManager.logger.info("Backups will be retained for " + getGMConfig().getBackupDuration() + " hours!");
		}
	}

	public void disableScheduler() {

		if (scheduler != null) {
			try {
				scheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
				scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
				scheduler.shutdown();
			} catch (Exception e) {
			}
			scheduler = null;
			GroupManager.logger.info("Scheduled Data Saving is disabled!");
		}
	}

	public WorldsHolder getWorldsHolder() {

		return worldsHolder;
	}

	/**
	 * Called when a command registered by this plugin is received.
	 * 
	 * @param sender
	 * @param cmd
	 * @param args
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		boolean playerCanDo = false;
		boolean isConsole = false;
		Player senderPlayer = null, targetPlayer = null;
		Group senderGroup = null;
		User senderUser = null;
		boolean isOpOverride = config.isOpOverride();
		boolean isAllowCommandBlocks = config.isAllowCommandBlocks();
		
		// PREVENT GM COMMANDS BEING USED ON COMMANDBLOCKS
		if (sender instanceof BlockCommandSender && !isAllowCommandBlocks) {
			Block block = ((BlockCommandSender)sender).getBlock();
			GroupManager.logger.warning(ChatColor.RED + "GM Commands can not be called from the CommandBlock at location: " + ChatColor.GREEN + block.getWorld().getName() + " - " + block.getX() + ", " + block.getY() + ", " + block.getZ());
		  	return true;
		}

		// DETERMINING PLAYER INFORMATION
		if (sender instanceof Player) {
			senderPlayer = (Player) sender;

			if (!lastError.isEmpty() && !commandLabel.equalsIgnoreCase("manload")) {
				sender.sendMessage(ChatColor.RED + "All commands are locked due to an error. " + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Check the log" + ChatColor.RESET + "" + ChatColor.RED + " and then try a '/manload'.");
				return true;
			}

			senderUser = worldsHolder.getWorldData(senderPlayer).getUser(senderPlayer.getName());
			senderGroup = senderUser.getGroup();
			isOpOverride = (isOpOverride && (senderPlayer.isOp() || worldsHolder.getWorldPermissions(senderPlayer).has(senderPlayer, "groupmanager.op")));

			System.out.println("[PLAYER_COMMAND] " + senderPlayer.getName() + ": /" + commandLabel + " " + Tasks.join(args, " "));
			if (isOpOverride || worldsHolder.getWorldPermissions(senderPlayer).has(senderPlayer, "groupmanager." + cmd.getName())) {
				playerCanDo = true;
			}
		} else if ((sender instanceof ConsoleCommandSender) || (sender instanceof RemoteConsoleCommandSender)) {

			if (!lastError.isEmpty() && !commandLabel.equalsIgnoreCase("manload")) {
				sender.sendMessage(ChatColor.RED + "All commands are locked due to an error. " + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Check the log" + ChatColor.RESET + "" + ChatColor.RED + " and then try a '/manload'.");
				return true;
			}

			isConsole = true;
		}

		// PERMISSIONS FOR COMMAND BEING LOADED
		dataHolder = null;
		permissionHandler = null;

		if (senderPlayer != null) {
			dataHolder = worldsHolder.getWorldData(senderPlayer);
		}

		String selectedWorld = selectedWorlds.get(sender);
		if (selectedWorld != null) {
			dataHolder = worldsHolder.getWorldData(selectedWorld);
		}

		if (dataHolder != null) {
			permissionHandler = dataHolder.getPermissionsHandler();
		}

		// VARIABLES USED IN COMMANDS

		int count;
		PermissionCheckResult permissionResult = null;
		ArrayList<User> removeList = null;
		String auxString = null;
		List<String> match = null;
		User auxUser = null;
		Group auxGroup = null;
		Group auxGroup2 = null;

		GroupManagerPermissions execCmd = null;
		try {
			execCmd = GroupManagerPermissions.valueOf(cmd.getName());
		} catch (Exception e) {
			// this error happened once with someone. now im prepared... i think
			GroupManager.logger.severe("===================================================");
			GroupManager.logger.severe("= ERROR REPORT START =");
			GroupManager.logger.severe("===================================================");
			GroupManager.logger.severe("= COPY AND PASTE THIS TO A GROUPMANAGER DEVELOPER =");
			GroupManager.logger.severe("===================================================");
			GroupManager.logger.severe(this.getDescription().getName());
			GroupManager.logger.severe(this.getDescription().getVersion());
			GroupManager.logger.severe("An error occured while trying to execute command:");
			GroupManager.logger.severe(cmd.getName());
			GroupManager.logger.severe("With " + args.length + " arguments:");
			for (String ar : args) {
				GroupManager.logger.severe(ar);
			}
			GroupManager.logger.severe("The field '" + cmd.getName() + "' was not found in enum.");
			GroupManager.logger.severe("And could not be parsed.");
			GroupManager.logger.severe("FIELDS FOUND IN ENUM:");
			for (GroupManagerPermissions val : GroupManagerPermissions.values()) {
				GroupManager.logger.severe(val.name());
			}
			GroupManager.logger.severe("===================================================");
			GroupManager.logger.severe("= ERROR REPORT ENDED =");
			GroupManager.logger.severe("===================================================");
			sender.sendMessage("An error occurred. Ask the admin to take a look at the console.");
		}

		if (isConsole || playerCanDo) {
			switch (execCmd) {
			case manuadd:

				// Validating arguments
				if ((args.length != 2) && (args.length != 3)) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player> <group> | optional [world])");
					return false;
				}

				// Select the relevant world (if specified)
				if (args.length == 3) {
					dataHolder = worldsHolder.getWorldData(args[2]);
					permissionHandler = dataHolder.getPermissionsHandler();
				}

				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}

				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}

				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				auxGroup = dataHolder.getGroup(args[1]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "Group not found!");
					return false;
				}
				if (auxGroup.isGlobal()) {
					sender.sendMessage(ChatColor.RED + "Players may not be members of GlobalGroups directly.");
					return false;
				}

				// Validating permissions
				if (!isConsole && !isOpOverride && (senderGroup != null ? permissionHandler.inGroup(auxUser.getName(), senderGroup.getName()) : false)) {
					sender.sendMessage(ChatColor.RED + "Can't modify a player with the same permissions as you, or higher.");
					return false;
				}
				if (!isConsole && !isOpOverride && (permissionHandler.hasGroupInInheritance(auxGroup, senderGroup.getName()))) {
					sender.sendMessage(ChatColor.RED + "The destination group can't be the same as yours, or higher.");
					return false;
				}
				if (!isConsole && !isOpOverride && (!permissionHandler.inGroup(senderUser.getName(), auxUser.getGroupName()) || !permissionHandler.inGroup(senderUser.getName(), auxGroup.getName()))) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player involving a group that you don't inherit.");
					return false;
				}

				// Seems OK
				auxUser.setGroup(auxGroup);
				if (!sender.hasPermission("groupmanager.notify.other") || (isConsole))
					sender.sendMessage(ChatColor.YELLOW + "You changed player '" + auxUser.getName() + "' group to '" + auxGroup.getName() + "' in world '" + dataHolder.getName() + "'.");

				return true;

			case manudel:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}

				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				// Validating permission
				if (!isConsole && !isOpOverride && (senderGroup != null ? permissionHandler.inGroup(auxUser.getName(), senderGroup.getName()) : false)) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player with same permissions as you, or higher.");
					return false;
				}
				// Seems OK
				dataHolder.removeUser(auxUser.getName());
				sender.sendMessage(ChatColor.YELLOW + "You changed player '" + auxUser.getName() + "' to default settings.");

				// If the player is online, this will create new data for the user.
				targetPlayer = this.getServer().getPlayer(auxUser.getName());
				if (targetPlayer != null)
					BukkitPermissions.updatePermissions(targetPlayer);

				return true;

			case manuaddsub:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender)) {
						sender.sendMessage(ChatColor.RED + "Couldn't retrieve your world. World selection is needed.");
						sender.sendMessage(ChatColor.RED + "Use /manselect <world>");
						return true;
					}
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player> <group>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}

				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				auxGroup = dataHolder.getGroup(args[1]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "Group not found!");
					return false;
				}
				// Validating permission
				if (!isConsole && !isOpOverride && (senderGroup != null ? permissionHandler.inGroup(auxUser.getName(), senderGroup.getName()) : false)) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player with same permissions as you, or higher.");
					return false;
				}
				// Seems OK
				if (auxUser.addSubGroup(auxGroup))
					sender.sendMessage(ChatColor.YELLOW + "You added subgroup '" + auxGroup.getName() + "' to player '" + auxUser.getName() + "'.");
				else
					sender.sendMessage(ChatColor.RED + "The subgroup '" + auxGroup.getName() + "' is already available to '" + auxUser.getName() + "'.");

				return true;

			case manudelsub:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/manudelsub <user> <group>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}

				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				auxGroup = dataHolder.getGroup(args[1]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "Group not found!");
					return false;
				}

				// Validating permission
				if (!isConsole && !isOpOverride && (senderGroup != null ? permissionHandler.inGroup(auxUser.getName(), senderGroup.getName()) : false)) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player with same permissions as you, or higher.");
					return false;
				}
				// Seems OK
				auxUser.removeSubGroup(auxGroup);
				sender.sendMessage(ChatColor.YELLOW + "You removed subgroup '" + auxGroup.getName() + "' from player '" + auxUser.getName() + "' list.");

				// targetPlayer = this.getServer().getPlayer(auxUser.getName());
				// if (targetPlayer != null)
				// BukkitPermissions.updatePermissions(targetPlayer);

				return true;

			case mangadd:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup != null) {
					sender.sendMessage(ChatColor.RED + "Group already exists!");
					return false;
				}
				// Seems OK
				auxGroup = dataHolder.createGroup(args[0]);
				sender.sendMessage(ChatColor.YELLOW + "You created a group named: " + auxGroup.getName());

				return true;

			case mangdel:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "Group not exists!");
					return false;
				}
				// Seems OK
				dataHolder.removeGroup(auxGroup.getName());
				sender.sendMessage(ChatColor.YELLOW + "You deleted a group named " + auxGroup.getName() + ", it's users are default group now.");

				BukkitPermissions.updateAllPlayers();

				return true;

			case manuaddp:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player> <permission>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}

				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				// Validating your permissions
				if (!isConsole && !isOpOverride && (senderGroup != null ? permissionHandler.inGroup(auxUser.getName(), senderGroup.getName()) : false)) {
					sender.sendMessage(ChatColor.RED + "Can't modify player with same group than you, or higher.");
					return false;
				}
				permissionResult = permissionHandler.checkFullUserPermission(senderUser, args[1]);
				if (!isConsole && !isOpOverride && (permissionResult.resultType.equals(PermissionCheckResult.Type.NOTFOUND) || permissionResult.resultType.equals(PermissionCheckResult.Type.NEGATION))) {
					sender.sendMessage(ChatColor.RED + "You can't add a permission you don't have.");
					return false;
				}
				// Validating permissions of user
				permissionResult = permissionHandler.checkUserOnlyPermission(auxUser, args[1]);
				if (args[1].startsWith("+")) {
					if (permissionResult.resultType.equals(PermissionCheckResult.Type.EXCEPTION)) {
						sender.sendMessage(ChatColor.RED + "The user already has direct access to that permission.");
						sender.sendMessage(ChatColor.RED + "Node: " + permissionResult.accessLevel);
						return false;
					}
				} else if (args[1].startsWith("-")) {
					if (permissionResult.resultType.equals(PermissionCheckResult.Type.EXCEPTION)) {
						sender.sendMessage(ChatColor.RED + "The user already has an exception for this node.");
						sender.sendMessage(ChatColor.RED + "Node: " + permissionResult.accessLevel);
						return false;
					} else if (permissionResult.resultType.equals(PermissionCheckResult.Type.NEGATION)) {
						sender.sendMessage(ChatColor.RED + "The user already has a matching node ");
						sender.sendMessage(ChatColor.RED + "Node: " + permissionResult.accessLevel);
						return false;
					}
				} else {
					if (permissionResult.resultType.equals(PermissionCheckResult.Type.FOUND)) {
						sender.sendMessage(ChatColor.RED + "The user already has direct access to that permission.");
						sender.sendMessage(ChatColor.RED + "Node: " + permissionResult.accessLevel);
						return false;
					}
				}
				// Seems OK
				auxUser.addPermission(args[1]);
				sender.sendMessage(ChatColor.YELLOW + "You added '" + args[1] + "' to player '" + auxUser.getName() + "' permissions.");

				targetPlayer = this.getServer().getPlayer(auxUser.getName());
				if (targetPlayer != null)
					BukkitPermissions.updatePermissions(targetPlayer);

				return true;

			case manudelp:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player> <permission>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}

				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				// Validating your permissions
				if (!isConsole && !isOpOverride && (senderGroup != null ? permissionHandler.inGroup(auxUser.getName(), senderGroup.getName()) : false)) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player with same group as you, or higher.");
					return false;
				}
				permissionResult = permissionHandler.checkFullUserPermission(senderUser, args[1]);
				if (!isConsole && !isOpOverride && (permissionResult.resultType.equals(PermissionCheckResult.Type.NOTFOUND) || permissionResult.resultType.equals(PermissionCheckResult.Type.NEGATION))) {
					sender.sendMessage(ChatColor.RED + "You can't remove a permission you don't have.");
					return false;
				}
				// Validating permissions of user
				permissionResult = permissionHandler.checkUserOnlyPermission(auxUser, args[1]);
				if (permissionResult.resultType.equals(PermissionCheckResult.Type.NOTFOUND)) {
					sender.sendMessage(ChatColor.RED + "The user doesn't have direct access to that permission.");
					return false;
				}
				if (!auxUser.hasSamePermissionNode(args[1])) {
					sender.sendMessage(ChatColor.RED + "This permission node doesn't match any node.");
					sender.sendMessage(ChatColor.RED + "But might match node: " + permissionResult.accessLevel);
					return false;
				}
				// Seems OK
				auxUser.removePermission(args[1]);
				sender.sendMessage(ChatColor.YELLOW + "You removed '" + args[1] + "' from player '" + auxUser.getName() + "' permissions.");

				targetPlayer = this.getServer().getPlayer(auxUser.getName());
				if (targetPlayer != null)
					BukkitPermissions.updatePermissions(targetPlayer);

				return true;

			case manulistp:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if ((args.length == 0) || (args.length > 2)) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player> (+))");
					return false;
				}

				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}

				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				// Validating permission
				// Seems OK
				auxString = "";
				for (String perm : auxUser.getPermissionList()) {
					auxString += perm + ", ";
				}
				if (auxString.lastIndexOf(",") > 0) {
					auxString = auxString.substring(0, auxString.lastIndexOf(","));
					sender.sendMessage(ChatColor.YELLOW + "The player '" + auxUser.getName() + "' has following permissions: " + ChatColor.WHITE + auxString);
					sender.sendMessage(ChatColor.YELLOW + "And all permissions from group: " + auxUser.getGroupName());
					auxString = "";
					for (String subGroup : auxUser.subGroupListStringCopy()) {
						auxString += subGroup + ", ";
					}
					if (auxString.lastIndexOf(",") > 0) {
						auxString = auxString.substring(0, auxString.lastIndexOf(","));
						sender.sendMessage(ChatColor.YELLOW + "And all permissions from subgroups: " + auxString);
					}
				} else {
					sender.sendMessage(ChatColor.YELLOW + "The player '" + auxUser.getName() + "' has no specific permissions.");
					sender.sendMessage(ChatColor.YELLOW + "Only all permissions from group: " + auxUser.getGroupName());
					auxString = "";
					for (String subGroup : auxUser.subGroupListStringCopy()) {
						auxString += subGroup + ", ";
					}
					if (auxString.lastIndexOf(",") > 0) {
						auxString = auxString.substring(0, auxString.lastIndexOf(","));
						sender.sendMessage(ChatColor.YELLOW + "And all permissions from subgroups: " + auxString);
					}
				}

				// bukkit perms
				if ((args.length == 2) && (args[1].equalsIgnoreCase("+"))) {
					targetPlayer = this.getServer().getPlayer(auxUser.getName());
					if (targetPlayer != null) {
						sender.sendMessage(ChatColor.YELLOW + "Superperms reports: ");
						for (String line : BukkitPermissions.listPerms(targetPlayer))
							sender.sendMessage(ChatColor.YELLOW + line);

					}
				}

				return true;

			case manucheckp:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player> <permission>)");
					return false;
				}

				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}

				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				targetPlayer = this.getServer().getPlayer(auxUser.getName());
				// Validating permission
				permissionResult = permissionHandler.checkFullGMPermission(auxUser, args[1], false);

				if (permissionResult.resultType.equals(PermissionCheckResult.Type.NOTFOUND)) {
					// No permissions found in GM so fall through and check Bukkit.
					sender.sendMessage(ChatColor.RED + "The player doesn't have access to that permission");

				} else {
					// This permission was found in groupmanager.
					if (permissionResult.owner instanceof User) {
						if (permissionResult.resultType.equals(PermissionCheckResult.Type.NEGATION)) {
							sender.sendMessage(ChatColor.RED + "The user has directly a negation node for that permission.");
						} else {
							sender.sendMessage(ChatColor.YELLOW + "The user has directly this permission.");
						}
						sender.sendMessage(ChatColor.YELLOW + "Permission Node: " + permissionResult.accessLevel);
					} else if (permissionResult.owner instanceof Group) {
						if (permissionResult.resultType.equals(PermissionCheckResult.Type.NEGATION)) {
							sender.sendMessage(ChatColor.RED + "The user inherits a negation permission from group: " + permissionResult.owner.getName());
						} else {
							sender.sendMessage(ChatColor.YELLOW + "The user inherits the permission from group: " + permissionResult.owner.getName());
						}
						sender.sendMessage(ChatColor.YELLOW + "Permission Node: " + permissionResult.accessLevel);
					}
				}

				// superperms
				if (targetPlayer != null) {
					sender.sendMessage(ChatColor.YELLOW + "SuperPerms reports Node: " + targetPlayer.hasPermission(args[1]) + ((!targetPlayer.hasPermission(args[1]) && targetPlayer.isPermissionSet(args[1])) ? " (Negated)": ""));
				}

				return true;

			case mangaddp:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group> <permission>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "The specified group does not exist!");
					return false;
				}
				// Validating your permissions
				permissionResult = permissionHandler.checkFullUserPermission(senderUser, args[1]);
				if (!isConsole && !isOpOverride && (permissionResult.resultType.equals(PermissionCheckResult.Type.NOTFOUND) || permissionResult.resultType.equals(PermissionCheckResult.Type.NEGATION))) {
					sender.sendMessage(ChatColor.RED + "You can't add a permission you don't have.");
					return false;
				}
				// Validating permissions of user
				permissionResult = permissionHandler.checkGroupOnlyPermission(auxGroup, args[1]);
				if (args[1].startsWith("+")) {
					if (permissionResult.resultType.equals(PermissionCheckResult.Type.EXCEPTION)) {
						sender.sendMessage(ChatColor.RED + "The group already has direct access to that permission.");
						sender.sendMessage(ChatColor.RED + "Node: " + permissionResult.accessLevel);
						return false;
					}
				} else if (args[1].startsWith("-")) {
					if (permissionResult.resultType.equals(PermissionCheckResult.Type.EXCEPTION)) {
						sender.sendMessage(ChatColor.RED + "The group already has an exception for this node.");
						sender.sendMessage(ChatColor.RED + "Node: " + permissionResult.accessLevel);
						return false;
					} else if (permissionResult.resultType.equals(PermissionCheckResult.Type.NEGATION)) {
						sender.sendMessage(ChatColor.RED + "The group already has a matching node.");
						sender.sendMessage(ChatColor.RED + "Node: " + permissionResult.accessLevel);
						return false;
					}
				} else {
					if (permissionResult.resultType.equals(PermissionCheckResult.Type.FOUND)) {
						sender.sendMessage(ChatColor.RED + "The group already has direct access to that permission.");
						sender.sendMessage(ChatColor.RED + "Node: " + permissionResult.accessLevel);
						return false;
					}
				}
				// Seems OK
				auxGroup.addPermission(args[1]);
				sender.sendMessage(ChatColor.YELLOW + "You added '" + args[1] + "' to group '" + auxGroup.getName() + "' permissions.");

				BukkitPermissions.updateAllPlayers();

				return true;

			case mangdelp:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group> <permission>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "The specified group does not exist!");
					return false;
				}
				// Validating your permissions
				permissionResult = permissionHandler.checkFullUserPermission(senderUser, args[1]);
				if (!isConsole && (permissionResult.resultType.equals(PermissionCheckResult.Type.NOTFOUND) || permissionResult.resultType.equals(PermissionCheckResult.Type.NEGATION))) {
					sender.sendMessage(ChatColor.RED + "Can't remove a permission you don't have.");
					return false;
				}
				// Validating permissions of user
				permissionResult = permissionHandler.checkGroupOnlyPermission(auxGroup, args[1]);
				if (permissionResult.resultType.equals(PermissionCheckResult.Type.NOTFOUND)) {
					sender.sendMessage(ChatColor.RED + "The group doesn't have direct access to that permission.");
					return false;
				}
				if (!auxGroup.hasSamePermissionNode(args[1])) {
					sender.sendMessage(ChatColor.RED + "This permission node doesn't match any node.");
					sender.sendMessage(ChatColor.RED + "But might match node: " + permissionResult.accessLevel);
					return false;
				}
				// Seems OK
				auxGroup.removePermission(args[1]);
				sender.sendMessage(ChatColor.YELLOW + "You removed '" + args[1] + "' from group '" + auxGroup.getName() + "' permissions.");

				BukkitPermissions.updateAllPlayers();

				return true;

			case manglistp:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "The specified group does not exist!");
					return false;
				}
				// Validating permission

				// Seems OK
				auxString = "";
				for (String perm : auxGroup.getPermissionList()) {
					auxString += perm + ", ";
				}
				if (auxString.lastIndexOf(",") > 0) {
					auxString = auxString.substring(0, auxString.lastIndexOf(","));
					sender.sendMessage(ChatColor.YELLOW + "The group '" + auxGroup.getName() + "' has following permissions: " + ChatColor.WHITE + auxString);
					auxString = "";
					for (String grp : auxGroup.getInherits()) {
						auxString += grp + ", ";
					}
					if (auxString.lastIndexOf(",") > 0) {
						auxString = auxString.substring(0, auxString.lastIndexOf(","));
						sender.sendMessage(ChatColor.YELLOW + "And all permissions from groups: " + auxString);
					}

				} else {
					sender.sendMessage(ChatColor.YELLOW + "The group '" + auxGroup.getName() + "' has no specific permissions.");
					auxString = "";
					for (String grp : auxGroup.getInherits()) {
						auxString += grp + ", ";
					}
					if (auxString.lastIndexOf(",") > 0) {
						auxString = auxString.substring(0, auxString.lastIndexOf(","));
						sender.sendMessage(ChatColor.YELLOW + "Only all permissions from groups: " + auxString);
					}

				}
				return true;

			case mangcheckp:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group> <permission>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "The specified group does not exist!");
					return false;
				}
				// Validating permission
				permissionResult = permissionHandler.checkGroupPermissionWithInheritance(auxGroup, args[1]);
				if (permissionResult.resultType.equals(PermissionCheckResult.Type.NOTFOUND)) {
					sender.sendMessage(ChatColor.RED + "The group doesn't have access to that permission");
					return false;
				}
				// Seems OK
				// auxString = permissionHandler.checkUserOnlyPermission(auxUser, args[1]);
				if (permissionResult.owner instanceof Group) {
					if (permissionResult.resultType.equals(PermissionCheckResult.Type.NEGATION)) {
						sender.sendMessage(ChatColor.RED + "The group inherits the negation permission from group: " + permissionResult.owner.getName());
					} else {
						sender.sendMessage(ChatColor.YELLOW + "The user inherits the permission from group: " + permissionResult.owner.getName());
					}
					sender.sendMessage(ChatColor.YELLOW + "Permission Node: " + permissionResult.accessLevel);

				}
				return true;

			case mangaddi:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group1> <group2>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "Group 1 does not exist!");
					return false;
				}
				auxGroup2 = dataHolder.getGroup(args[1]);
				if (auxGroup2 == null) {
					sender.sendMessage(ChatColor.RED + "Group 2 does not exist!");
					return false;
				}
				if (auxGroup.isGlobal()) {
					sender.sendMessage(ChatColor.RED + "GlobalGroups do NOT support inheritance.");
					return false;
				}

				// Validating permission
				if (permissionHandler.hasGroupInInheritance(auxGroup, auxGroup2.getName())) {
					sender.sendMessage(ChatColor.RED + "Group " + auxGroup.getName() + " already inherits " + auxGroup2.getName() + " (might not be directly)");
					return false;
				}
				// Seems OK
				auxGroup.addInherits(auxGroup2);
				sender.sendMessage(ChatColor.RED + "Group " + auxGroup2.getName() + " is now in " + auxGroup.getName() + " inheritance list.");

				BukkitPermissions.updateAllPlayers();

				return true;

			case mangdeli:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group1> <group2>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "Group 1 does not exist!");
					return false;
				}
				auxGroup2 = dataHolder.getGroup(args[1]);
				if (auxGroup2 == null) {
					sender.sendMessage(ChatColor.RED + "Group 2 does not exist!");
					return false;
				}
				if (auxGroup.isGlobal()) {
					sender.sendMessage(ChatColor.RED + "GlobalGroups do NOT support inheritance.");
					return false;
				}

				// Validating permission
				if (!permissionHandler.hasGroupInInheritance(auxGroup, auxGroup2.getName())) {
					sender.sendMessage(ChatColor.RED + "Group " + auxGroup.getName() + " does not inherits " + auxGroup2.getName() + ".");
					return false;
				}
				if (!auxGroup.getInherits().contains(auxGroup2.getName())) {
					sender.sendMessage(ChatColor.RED + "Group " + auxGroup.getName() + " does not inherits " + auxGroup2.getName() + " directly.");
					return false;
				}
				// Seems OK
				auxGroup.removeInherits(auxGroup2.getName());
				sender.sendMessage(ChatColor.RED + "Group " + auxGroup2.getName() + " was removed from " + auxGroup.getName() + " inheritance list.");

				BukkitPermissions.updateAllPlayers();

				return true;

			case manuaddv:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <user> <variable> <value>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}

				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				// Validating permission
				// Seems OK
				auxString = "";
				for (int i = 2; i < args.length; i++) {
					auxString += args[i];
					if ((i + 1) < args.length) {
						auxString += " ";
					}
				}
				auxUser.getVariables().addVar(args[1], Variables.parseVariableValue(auxString));
				sender.sendMessage(ChatColor.YELLOW + "Variable " + ChatColor.GOLD + args[1] + ChatColor.YELLOW + ":'" + ChatColor.GREEN + auxString + ChatColor.YELLOW + "' added to the user " + auxUser.getName());

				return true;

			case manudelv:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <user> <variable>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}

				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				// Validating permission
				if (!auxUser.getVariables().hasVar(args[1])) {
					sender.sendMessage(ChatColor.RED + "The user doesn't have directly that variable!");
				}
				// Seems OK
				auxUser.getVariables().removeVar(args[1]);
				sender.sendMessage(ChatColor.YELLOW + "Variable " + ChatColor.GOLD + args[1] + ChatColor.YELLOW + " removed from the user " + ChatColor.GREEN + auxUser.getName());

				return true;

			case manulistv:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <user>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}
				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				// Validating permission
				// Seems OK
				auxString = "";
				for (String varKey : auxUser.getVariables().getVarKeyList()) {
					Object o = auxUser.getVariables().getVarObject(varKey);
					auxString += ChatColor.GOLD + varKey + ChatColor.WHITE + ":'" + ChatColor.GREEN + o.toString() + ChatColor.WHITE + "', ";
				}
				if (auxString.lastIndexOf(",") > 0) {
					auxString = auxString.substring(0, auxString.lastIndexOf(","));
				}
				sender.sendMessage(ChatColor.YELLOW + "Variables of user " + auxUser.getName() + ": ");
				sender.sendMessage(auxString + ".");
				sender.sendMessage(ChatColor.YELLOW + "Plus all variables from group: " + auxUser.getGroupName());

				return true;

			case manucheckv:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <user> <variable>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}
				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				// Validating permission
				auxGroup = auxUser.getGroup();
				auxGroup2 = permissionHandler.nextGroupWithVariable(auxGroup, args[1]);

				if (!auxUser.getVariables().hasVar(args[1])) {
					// Check sub groups
					if (!auxUser.isSubGroupsEmpty() && auxGroup2 == null)
						for (Group subGroup : auxUser.subGroupListCopy()) {
							auxGroup2 = permissionHandler.nextGroupWithVariable(subGroup, args[1]);
							if (auxGroup2 != null)
								continue;
						}
					if (auxGroup2 == null) {
						sender.sendMessage(ChatColor.RED + "The user doesn't have access to that variable!");
						return false;
					}
				}
				// Seems OK
				if (auxUser.getVariables().hasVar(auxString)) {
					sender.sendMessage(ChatColor.YELLOW + "The value of variable '" + ChatColor.GOLD + args[1] + ChatColor.YELLOW + "' is: '" + ChatColor.GREEN + auxUser.getVariables().getVarObject(args[1]).toString() + ChatColor.WHITE + "'");
					sender.sendMessage(ChatColor.YELLOW + "This user own directly the variable");
				}
				sender.sendMessage(ChatColor.YELLOW + "The value of variable '" + ChatColor.GOLD + args[1] + ChatColor.YELLOW + "' is: '" + ChatColor.GREEN + auxGroup2.getVariables().getVarObject(args[1]).toString() + ChatColor.WHITE + "'");
				if (!auxGroup.equals(auxGroup2)) {
					sender.sendMessage(ChatColor.YELLOW + "And the value was inherited from group: " + ChatColor.GREEN + auxGroup2.getName());
				}

				return true;

			case mangaddv:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group> <variable> <value>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "The specified group does not exist!");
					return false;
				}
				if (auxGroup.isGlobal()) {
					sender.sendMessage(ChatColor.RED + "GlobalGroups do NOT support Info Nodes.");
					return false;
				}
				// Validating permission
				// Seems OK
				auxString = "";
				for (int i = 2; i < args.length; i++) {
					auxString += args[i];
					if ((i + 1) < args.length) {
						auxString += " ";
					}
				}
				auxGroup.getVariables().addVar(args[1], Variables.parseVariableValue(auxString));
				sender.sendMessage(ChatColor.YELLOW + "Variable " + ChatColor.GOLD + args[1] + ChatColor.YELLOW + ":'" + ChatColor.GREEN + auxString + ChatColor.YELLOW + "' added to the group " + auxGroup.getName());

				return true;

			case mangdelv:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group> <variable>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "The specified group does not exist!");
					return false;
				}
				if (auxGroup.isGlobal()) {
					sender.sendMessage(ChatColor.RED + "GlobalGroups do NOT support Info Nodes.");
					return false;
				}
				// Validating permission
				if (!auxGroup.getVariables().hasVar(args[1])) {
					sender.sendMessage(ChatColor.RED + "The group doesn't have directly that variable!");
				}
				// Seems OK
				auxGroup.getVariables().removeVar(args[1]);
				sender.sendMessage(ChatColor.YELLOW + "Variable " + ChatColor.GOLD + args[1] + ChatColor.YELLOW + " removed from the group " + ChatColor.GREEN + auxGroup.getName());

				return true;

			case manglistv:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "The specified group does not exist!");
					return false;
				}
				if (auxGroup.isGlobal()) {
					sender.sendMessage(ChatColor.RED + "GlobalGroups do NOT support Info Nodes.");
					return false;
				}
				// Validating permission
				// Seems OK
				auxString = "";
				for (String varKey : auxGroup.getVariables().getVarKeyList()) {
					Object o = auxGroup.getVariables().getVarObject(varKey);
					auxString += ChatColor.GOLD + varKey + ChatColor.WHITE + ":'" + ChatColor.GREEN + o.toString() + ChatColor.WHITE + "', ";
				}
				if (auxString.lastIndexOf(",") > 0) {
					auxString = auxString.substring(0, auxString.lastIndexOf(","));
				}
				sender.sendMessage(ChatColor.YELLOW + "Variables of group " + auxGroup.getName() + ": ");
				sender.sendMessage(auxString + ".");
				auxString = "";
				for (String grp : auxGroup.getInherits()) {
					auxString += grp + ", ";
				}
				if (auxString.lastIndexOf(",") > 0) {
					auxString = auxString.substring(0, auxString.lastIndexOf(","));
					sender.sendMessage(ChatColor.YELLOW + "Plus all variables from groups: " + auxString);
				}

				return true;

			case mangcheckv:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <group> <variable>)");
					return false;
				}
				auxGroup = dataHolder.getGroup(args[0]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "The specified group does not exist!");
					return false;
				}
				if (auxGroup.isGlobal()) {
					sender.sendMessage(ChatColor.RED + "GlobalGroups do NOT support Info Nodes.");
					return false;
				}
				// Validating permission
				auxGroup2 = permissionHandler.nextGroupWithVariable(auxGroup, args[1]);
				if (auxGroup2 == null) {
					sender.sendMessage(ChatColor.RED + "The group doesn't have access to that variable!");
				}
				// Seems OK
				sender.sendMessage(ChatColor.YELLOW + "The value of variable '" + ChatColor.GOLD + args[1] + ChatColor.YELLOW + "' is: '" + ChatColor.GREEN + auxGroup2.getVariables().getVarObject(args[1]).toString() + ChatColor.WHITE + "'");
				if (!auxGroup.equals(auxGroup2)) {
					sender.sendMessage(ChatColor.YELLOW + "And the value was inherited from group: " + ChatColor.GREEN + auxGroup2.getName());
				}

				return true;

			case manwhois:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}
				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				// Seems OK
				sender.sendMessage(ChatColor.YELLOW + "Name: " + ChatColor.GREEN + auxUser.getName());
				sender.sendMessage(ChatColor.YELLOW + "Group: " + ChatColor.GREEN + auxUser.getGroup().getName());
				// Compile a list of subgroups
				auxString = "";
				for (String subGroup : auxUser.subGroupListStringCopy()) {
					auxString += subGroup + ", ";
				}
				if (auxString.lastIndexOf(",") > 0) {
					auxString = auxString.substring(0, auxString.lastIndexOf(","));
					sender.sendMessage(ChatColor.YELLOW + "subgroups: " + auxString);
				}

				sender.sendMessage(ChatColor.YELLOW + "Overloaded: " + ChatColor.GREEN + dataHolder.isOverloaded(auxUser.getName()));
				auxGroup = dataHolder.surpassOverload(auxUser.getName()).getGroup();
				if (!auxGroup.equals(auxUser.getGroup())) {
					sender.sendMessage(ChatColor.YELLOW + "Original Group: " + ChatColor.GREEN + auxGroup.getName());
				}
				// victim.permissions.add(args[1]);
				return true;

			case tempadd:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}
				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				// Validating permission
				if (!isConsole && !isOpOverride && (senderGroup != null ? permissionHandler.inGroup(auxUser.getName(), senderGroup.getName()) : false)) {
					sender.sendMessage(ChatColor.RED + "Can't modify player with same permissions than you, or higher.");
					return false;
				}
				// Seems OK
				if (overloadedUsers.get(dataHolder.getName().toLowerCase()) == null) {
					overloadedUsers.put(dataHolder.getName().toLowerCase(), new ArrayList<User>());
				}
				dataHolder.overloadUser(auxUser.getName());
				overloadedUsers.get(dataHolder.getName().toLowerCase()).add(dataHolder.getUser(auxUser.getName()));
				sender.sendMessage(ChatColor.YELLOW + "Player overloaded!");

				return true;

			case tempdel:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}
				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				// Validating permission
				if (!isConsole && !isOpOverride && (senderGroup != null ? permissionHandler.inGroup(auxUser.getName(), senderGroup.getName()) : false)) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player with same permissions as you, or higher.");
					return false;
				}
				// Seems OK
				if (overloadedUsers.get(dataHolder.getName().toLowerCase()) == null) {
					overloadedUsers.put(dataHolder.getName().toLowerCase(), new ArrayList<User>());
				}
				dataHolder.removeOverload(auxUser.getName());
				if (overloadedUsers.get(dataHolder.getName().toLowerCase()).contains(auxUser)) {
					overloadedUsers.get(dataHolder.getName().toLowerCase()).remove(auxUser);
				}
				sender.sendMessage(ChatColor.YELLOW + "You removed that player's overload. He's back to normal!");

				return true;

			case templist:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// WORKING
				auxString = "";
				removeList = new ArrayList<User>();
				count = 0;
				for (User u : overloadedUsers.get(dataHolder.getName().toLowerCase())) {
					if (!dataHolder.isOverloaded(u.getName())) {
						removeList.add(u);
					} else {
						auxString += u.getName() + ", ";
						count++;
					}
				}
				if (count == 0) {
					sender.sendMessage(ChatColor.YELLOW + "There are no users in overload mode.");
					return true;
				}
				auxString = auxString.substring(0, auxString.lastIndexOf(","));
				if (overloadedUsers.get(dataHolder.getName().toLowerCase()) == null) {
					overloadedUsers.put(dataHolder.getName().toLowerCase(), new ArrayList<User>());
				}
				overloadedUsers.get(dataHolder.getName().toLowerCase()).removeAll(removeList);
				sender.sendMessage(ChatColor.YELLOW + " " + count + " Users in overload mode: " + ChatColor.WHITE + auxString);

				return true;

			case tempdelall:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// WORKING
				removeList = new ArrayList<User>();
				count = 0;
				for (User u : overloadedUsers.get(dataHolder.getName().toLowerCase())) {
					if (dataHolder.isOverloaded(u.getName())) {
						dataHolder.removeOverload(u.getName());
						count++;
					}
				}
				if (count == 0) {
					sender.sendMessage(ChatColor.YELLOW + "There are no users in overload mode.");
					return true;
				}
				if (overloadedUsers.get(dataHolder.getName().toLowerCase()) == null) {
					overloadedUsers.put(dataHolder.getName().toLowerCase(), new ArrayList<User>());
				}
				overloadedUsers.get(dataHolder.getName().toLowerCase()).clear();
				sender.sendMessage(ChatColor.YELLOW + " " + count + "All users in overload mode are now normal again.");

				return true;

			case mansave:

				boolean forced = false;

				if ((args.length == 1) && (args[0].equalsIgnoreCase("force")))
					forced = true;

				try {
					worldsHolder.saveChanges(forced);
					sender.sendMessage(ChatColor.YELLOW + "All changes were saved.");
				} catch (IllegalStateException ex) {
					sender.sendMessage(ChatColor.RED + ex.getMessage());
				}
				return true;

			case manload:

				/**
				 * Attempt to reload a specific world
				 */
				if (args.length > 0) {

					if (!lastError.isEmpty()) {
						sender.sendMessage(ChatColor.RED + "All commands are locked due to an error. " + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Check the log" + ChatColor.RESET + "" + ChatColor.RED + " and then try a '/manload'.");
						return true;
					}

					auxString = "";
					for (int i = 0; i < args.length; i++) {
						auxString += args[i];
						if ((i + 1) < args.length) {
							auxString += " ";
						}
					}

					isLoaded = false; // Disable Bukkit Perms update and event triggers

					globalGroups.load();
					worldsHolder.loadWorld(auxString);

					sender.sendMessage("The request to reload world '" + auxString + "' was attempted.");

					isLoaded = true;

					BukkitPermissions.reset();

				} else {

					/**
					 * Reload all settings and data as no world was specified.
					 */

					/*
					 * Attempting a fresh load.
					 */
					onDisable(true);
					onEnable(true);

					sender.sendMessage("All settings and worlds were reloaded!");
				}

				/**
				 * Fire an event as none will have been triggered in the reload.
				 */
				if (GroupManager.isLoaded())
					GroupManager.getGMEventHandler().callEvent(GMSystemEvent.Action.RELOADED);

				return true;

			case listgroups:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// WORKING
				auxString = "";
				for (Group g : dataHolder.getGroupList()) {
					auxString += g.getName() + ", ";
				}
				for (Group g : getGlobalGroups().getGroupList()) {
					auxString += g.getName() + ", ";
				}
				if (auxString.lastIndexOf(",") > 0) {
					auxString = auxString.substring(0, auxString.lastIndexOf(","));
				}
				sender.sendMessage(ChatColor.YELLOW + " Groups Available: " + ChatColor.WHITE + auxString);

				return true;

			case manpromote:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player> <group>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}
				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				auxGroup = dataHolder.getGroup(args[1]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "Group not found!");
					return false;
				}
				if (auxGroup.isGlobal()) {
					sender.sendMessage(ChatColor.RED + "Players may not be members of GlobalGroups directly.");
					return false;
				}
				// Validating permission
				if (!isConsole && !isOpOverride && (senderGroup != null ? permissionHandler.inGroup(auxUser.getName(), senderGroup.getName()) : false)) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player with same permissions as you, or higher.");
					return false;
				}
				if (!isConsole && !isOpOverride && (permissionHandler.hasGroupInInheritance(auxGroup, senderGroup.getName()))) {
					sender.sendMessage(ChatColor.RED + "The destination group can't be the same as yours, or higher.");
					return false;
				}
				if (!isConsole && !isOpOverride && (!permissionHandler.inGroup(senderUser.getName(), auxUser.getGroupName()) || !permissionHandler.inGroup(senderUser.getName(), auxGroup.getName()))) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player involving a group that you don't inherit.");
					return false;
				}
				if (!isConsole && !isOpOverride && (!permissionHandler.hasGroupInInheritance(auxUser.getGroup(), auxGroup.getName()) && !permissionHandler.hasGroupInInheritance(auxGroup, auxUser.getGroupName()))) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player using groups with different heritage line.");
					return false;
				}
				if (!isConsole && !isOpOverride && (!permissionHandler.hasGroupInInheritance(auxGroup, auxUser.getGroupName()))) {
					sender.sendMessage(ChatColor.RED + "The new group must be a higher rank.");
					return false;
				}
				// Seems OK
				auxUser.setGroup(auxGroup);
				if (!sender.hasPermission("groupmanager.notify.other") || (isConsole))
					sender.sendMessage(ChatColor.YELLOW + "You changed " + auxUser.getName() + " group to " + auxGroup.getName() + ".");

				return true;

			case mandemote:
				// Validating state of sender
				if (dataHolder == null || permissionHandler == null) {
					if (!setDefaultWorldHandler(sender))
						return true;
				}
				// Validating arguments
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/<command> <player> <group>)");
					return false;
				}
				if ((validateOnlinePlayer) && ((match = validatePlayer(args[0], sender)) == null)) {
					return false;
				}
				if (match != null) {
					auxUser = dataHolder.getUser(match.get(0));
				} else {
					auxUser = dataHolder.getUser(args[0]);
				}
				auxGroup = dataHolder.getGroup(args[1]);
				if (auxGroup == null) {
					sender.sendMessage(ChatColor.RED + "Group not found!");
					return false;
				}
				if (auxGroup.isGlobal()) {
					sender.sendMessage(ChatColor.RED + "Players may not be members of GlobalGroups directly.");
					return false;
				}
				// Validating permission
				if (!isConsole && !isOpOverride && (senderGroup != null ? permissionHandler.inGroup(auxUser.getName(), senderGroup.getName()) : false)) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player with same permissions as you, or higher.");
					return false;
				}
				if (!isConsole && !isOpOverride && (permissionHandler.hasGroupInInheritance(auxGroup, senderGroup.getName()))) {
					sender.sendMessage(ChatColor.RED + "The destination group can't be the same as yours, or higher.");
					return false;
				}
				if (!isConsole && !isOpOverride && (!permissionHandler.inGroup(senderUser.getName(), auxUser.getGroupName()) || !permissionHandler.inGroup(senderUser.getName(), auxGroup.getName()))) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player involving a group that you don't inherit.");
					return false;
				}
				if (!isConsole && !isOpOverride && (!permissionHandler.hasGroupInInheritance(auxUser.getGroup(), auxGroup.getName()) && !permissionHandler.hasGroupInInheritance(auxGroup, auxUser.getGroupName()))) {
					sender.sendMessage(ChatColor.RED + "You can't modify a player using groups with different inheritage line.");
					return false;
				}
				if (!isConsole && !isOpOverride && (permissionHandler.hasGroupInInheritance(auxGroup, auxUser.getGroupName()))) {
					sender.sendMessage(ChatColor.RED + "The new group must be a lower rank.");
					return false;
				}
				// Seems OK
				auxUser.setGroup(auxGroup);
				if (!sender.hasPermission("groupmanager.notify.other") || (isConsole))
					sender.sendMessage(ChatColor.YELLOW + "You changed " + auxUser.getName() + " group to " + auxGroup.getName() + ".");

				return true;

			case mantogglevalidate:
				validateOnlinePlayer = !validateOnlinePlayer;
				sender.sendMessage(ChatColor.YELLOW + "Validate if player is online, now set to: " + Boolean.toString(validateOnlinePlayer));
				if (!validateOnlinePlayer) {
					sender.sendMessage(ChatColor.GOLD + "From now on you can edit players that are not connected... BUT:");
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "From now on you should type the whole name of the player, correctly.");
				}
				return true;
			case mantogglesave:
				if (scheduler == null) {
					enableScheduler();
					sender.sendMessage(ChatColor.YELLOW + "The auto-saving is enabled!");
				} else {
					disableScheduler();
					sender.sendMessage(ChatColor.YELLOW + "The auto-saving is disabled!");
				}
				return true;
			case manworld:
				auxString = selectedWorlds.get(sender);
				if (auxString != null) {
					sender.sendMessage(ChatColor.YELLOW + "You have the world '" + dataHolder.getName() + "' in your selection.");
				} else {
					if (dataHolder == null) {
						sender.sendMessage(ChatColor.YELLOW + "There is no world selected. And no world is available now.");
					} else {
						sender.sendMessage(ChatColor.YELLOW + "You don't have a world in your selection..");
						sender.sendMessage(ChatColor.YELLOW + "Working with the direct world where your player is.");
						sender.sendMessage(ChatColor.YELLOW + "Your world now uses permissions of world name: '" + dataHolder.getName() + "' ");
					}
				}

				return true;

			case manselect:
				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/manselect <world>)");
					sender.sendMessage(ChatColor.YELLOW + "Worlds available: ");
					ArrayList<OverloadedWorldHolder> worlds = worldsHolder.allWorldsDataList();
					auxString = "";
					for (int i = 0; i < worlds.size(); i++) {
						auxString += worlds.get(i).getName();
						if ((i + 1) < worlds.size()) {
							auxString += ", ";
						}
					}
					sender.sendMessage(ChatColor.YELLOW + auxString);
					return false;
				}
				auxString = "";
				for (int i = 0; i < args.length; i++) {
					if (args[i] == null) {
						logger.warning("Bukkit gave invalid arguments array! Cmd: " + cmd.getName() + " args.length: " + args.length);
						return false;
					}
					auxString += args[i];
					if (i < (args.length - 1)) {
						auxString += " ";
					}
				}
				dataHolder = worldsHolder.getWorldData(auxString);
				permissionHandler = dataHolder.getPermissionsHandler();
				selectedWorlds.put(sender, dataHolder.getName());
				sender.sendMessage(ChatColor.YELLOW + "You have selected world '" + dataHolder.getName() + "'.");

				return true;

			case manclear:
				if (args.length != 0) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count!");
					return false;
				}
				selectedWorlds.remove(sender);
				sender.sendMessage(ChatColor.YELLOW + "You have removed your world selection. Working with current world(if possible).");

				return true;
				
			case mancheckw:
				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "Review your arguments count! (/mancheckw <world>)");
					sender.sendMessage(ChatColor.YELLOW + "Worlds available: ");
					ArrayList<OverloadedWorldHolder> worlds = worldsHolder.allWorldsDataList();
					auxString = "";
					for (int i = 0; i < worlds.size(); i++) {
						auxString += worlds.get(i).getName();
						if ((i + 1) < worlds.size()) {
							auxString += ", ";
						}
					}
					sender.sendMessage(ChatColor.YELLOW + auxString);
					return false;
				}
				
				auxString = "";
				for (int i = 0; i < args.length; i++) {
					if (args[i] == null) {
						logger.warning("Bukkit gave invalid arguments array! Cmd: " + cmd.getName() + " args.length: " + args.length);
						return false;
					}
					auxString += args[i];
					if (i < (args.length - 1)) {
						auxString += " ";
					}
				}
				dataHolder = worldsHolder.getWorldData(auxString);
				
				sender.sendMessage(ChatColor.YELLOW + "You have selected world '" + dataHolder.getName() + "'.");
				sender.sendMessage(ChatColor.YELLOW + "This world is using the following data files..");
				sender.sendMessage(ChatColor.YELLOW + "Groups:" + ChatColor.GREEN + " " + dataHolder.getGroupsFile().getAbsolutePath());
				sender.sendMessage(ChatColor.YELLOW + "Users:" + ChatColor.GREEN + " " + dataHolder.getUsersFile().getAbsolutePath());

				return true;

			default:
				break;
			}
		}
		sender.sendMessage(ChatColor.RED + "You are not allowed to use that command.");
		return false;
	}

	/**
	 * Sets up the default world for use.
	 */
	private boolean setDefaultWorldHandler(CommandSender sender) {

		dataHolder = worldsHolder.getWorldData(worldsHolder.getDefaultWorld().getName());
		permissionHandler = dataHolder.getPermissionsHandler();

		if ((dataHolder != null) && (permissionHandler != null)) {
			selectedWorlds.put(sender, dataHolder.getName());
			sender.sendMessage(ChatColor.RED + "Couldn't retrieve your world. Default world '" + worldsHolder.getDefaultWorld().getName() + "' selected.");
			return true;
		}

		sender.sendMessage(ChatColor.RED + "Couldn't retrieve your world. World selection is needed.");
		sender.sendMessage(ChatColor.RED + "Use /manselect <world>");
		return false;

	}

	/**
	 * Send confirmation of a group change. using permission nodes...
	 * 
	 * groupmanager.notify.self groupmanager.notify.other
	 * 
	 * @param name
	 * @param msg
	 */
	public static void notify(String name, String msg) {

		Player player = Bukkit.getServer().getPlayerExact(name);

		for (Player test : Bukkit.getServer().getOnlinePlayers()) {
			if (!test.equals(player)) {
				if (test.hasPermission("groupmanager.notify.other"))
					test.sendMessage(ChatColor.YELLOW + name + " was" + msg);
			} else if ((player != null) && ((player.hasPermission("groupmanager.notify.self")) || (player.hasPermission("groupmanager.notify.other"))))
				player.sendMessage(ChatColor.YELLOW + "You were" + msg);
		}

	}

	/**
	 * Load a List of players matching the name given. If none online, check
	 * Offline.
	 * 
	 * @param playerName, sender
	 * @return true if a single match is found
	 */
	private List<String> validatePlayer(String playerName, CommandSender sender) {

		List<Player> players = new ArrayList<Player>();
		List<String> match = new ArrayList<String>();

		players = this.getServer().matchPlayer(playerName);
		if (players.isEmpty()) {
			// Check for an offline player (exact match).
			if (Arrays.asList(this.getServer().getOfflinePlayers()).contains(Bukkit.getOfflinePlayer(playerName))) {
				match.add(playerName);
			} else {
				// look for partial matches
				for (OfflinePlayer offline : this.getServer().getOfflinePlayers()) {
					if (offline.getName().toLowerCase().startsWith(playerName.toLowerCase()))
						match.add(offline.getName());
				}
			}

		} else {
			for (Player player : players) {
				match.add(player.getName());
			}
		}

		if (match.isEmpty() || match == null) {
			sender.sendMessage(ChatColor.RED + "Player not found!");
			return null;
		} else if (match.size() > 1) {
			sender.sendMessage(ChatColor.RED + "Too many matches found! (" + match.toString() + ")");
			return null;
		}

		return match;

	}

	/**
	 * @return the config
	 */
	public GMConfiguration getGMConfig() {

		return config;
	}

	/**
	 * @return the backupFolder
	 */
	public File getBackupFolder() {

		return backupFolder;
	}

	public static GlobalGroups getGlobalGroups() {

		return globalGroups;

	}

	public static GroupManagerEventHandler getGMEventHandler() {

		return GMEventHandler;
	}

	public static void setGMEventHandler(GroupManagerEventHandler gMEventHandler) {

		GMEventHandler = gMEventHandler;
	}
}
