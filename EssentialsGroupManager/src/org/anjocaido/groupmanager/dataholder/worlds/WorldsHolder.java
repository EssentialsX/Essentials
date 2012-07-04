/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.dataholder.worlds;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.anjocaido.groupmanager.utils.Tasks;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

/**
 * 
 * @author gabrielcouto
 */
public class WorldsHolder {

	/**
	 * Map with instances of loaded worlds.
	 */
	private Map<String, OverloadedWorldHolder> worldsData = new HashMap<String, OverloadedWorldHolder>();

	/**
	 * Map of mirrors: <nonExistingWorldName, existingAndLoadedWorldName>
	 * The key is the mirror.
	 * The object is the mirrored.
	 * 
	 * Mirror shows the same data of mirrored.
	 */
	private Map<String, String> mirrorsGroup = new HashMap<String, String>();
	private Map<String, String> mirrorsUser = new HashMap<String, String>();

	private String serverDefaultWorldName;
	private GroupManager plugin;
	private File worldsFolder;

	/**
	 * 
	 * @param plugin
	 */
	public WorldsHolder(GroupManager plugin) {

		this.plugin = plugin;
		resetWorldsHolder();
	}
	
	public void resetWorldsHolder() {
		
		worldsData = new HashMap<String, OverloadedWorldHolder>();
		mirrorsGroup = new HashMap<String, String>();
		mirrorsUser = new HashMap<String, String>();
		
		// Setup folders and check files exist for the primary world
		verifyFirstRun();
		initialLoad();
		if (serverDefaultWorldName == null)
			throw new IllegalStateException("There is no default group! OMG!");
	}

	private void initialLoad() {

		// load the initial world
		initialWorldLoading();
		// Configure and load any mirrors and additional worlds as defined in config.yml
		mirrorSetUp();
		// search the worlds folder for any manually created worlds (not listed in config.yml)
		loadAllSearchedWorlds();
	}

	private void initialWorldLoading() {

		//Load the default world
		loadWorld(serverDefaultWorldName);
		//defaultWorld = getUpdatedWorldData(serverDefaultWorldName);
	}

	private void loadAllSearchedWorlds() {

		/*
		 * Read all known worlds from Bukkit
		 * Create the data files if they don't already exist,
		 * and they are not mirrored.
		 */
		for (World world : plugin.getServer().getWorlds())
			if ((!worldsData.containsKey(world.getName().toLowerCase())) && ((!mirrorsGroup.containsKey(world.getName().toLowerCase())) || (!mirrorsUser.containsKey(world.getName().toLowerCase()))))
				setupWorldFolder(world.getName());
		/*
		 * Loop over all folders within the worlds folder
		 * and attempt to load the world data
		 */
		for (File folder : worldsFolder.listFiles()) {
			if (folder.isDirectory() && !folder.getName().startsWith(".")) {
				GroupManager.logger.info("World Found: " + folder.getName());

				/*
				 * don't load any worlds which are already loaded
				 * or fully mirrored worlds that don't need data.
				 */
				if (!worldsData.containsKey(folder.getName().toLowerCase()) && ((!mirrorsGroup.containsKey(folder.getName().toLowerCase())) || (!mirrorsUser.containsKey(folder.getName().toLowerCase())))) {
					/*
					 * Call setupWorldFolder to check case sensitivity
					 * and convert to lower case, before we attempt to load this
					 * world.
					 */
					setupWorldFolder(folder.getName());
					loadWorld(folder.getName().toLowerCase());
				}

			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void mirrorSetUp() {

		mirrorsGroup.clear();
		mirrorsUser.clear();
		Map<String, Object> mirrorsMap = plugin.getGMConfig().getMirrorsMap();

		HashSet<String> mirroredWorlds = new HashSet<String>();

		if (mirrorsMap != null) {
			for (String source : mirrorsMap.keySet()) {
				// Make sure all non mirrored worlds have a set of data files.
				setupWorldFolder(source);
				// Load the world data
				if (!worldsData.containsKey(source.toLowerCase()))
					loadWorld(source);

				if (mirrorsMap.get(source) instanceof ArrayList) {
					ArrayList mirrorList = (ArrayList) mirrorsMap.get(source);

					// These worlds fully mirror their parent
					for (Object o : mirrorList) {
						String world = o.toString().toLowerCase();
						if (world != serverDefaultWorldName) {
							try {
								mirrorsGroup.remove(world);
								mirrorsUser.remove(world);
							} catch (Exception e) {
							}
							mirrorsGroup.put(world, getWorldData(source).getName());
							mirrorsUser.put(world, getWorldData(source).getName());

							// Track this world so we can create a datasource for it later
							mirroredWorlds.add(o.toString());

						} else
							GroupManager.logger.log(Level.WARNING, "Mirroring error with " + o.toString() + ". Recursive loop detected!");
					}
				} else if (mirrorsMap.get(source) instanceof MemorySection) {
					MemorySection subSection = (MemorySection) mirrorsMap.get(source);

					for (String key : subSection.getKeys(true)) {

						if (key.toLowerCase() != serverDefaultWorldName) {

							if (subSection.get(key) instanceof ArrayList) {
								ArrayList mirrorList = (ArrayList) subSection.get(key);

								// These worlds have defined mirroring
								for (Object o : mirrorList) {
									String type = o.toString().toLowerCase();
									try {
										if (type.equals("groups"))
											mirrorsGroup.remove(key.toLowerCase());

										if (type.equals("users"))
											mirrorsUser.remove(key.toLowerCase());

									} catch (Exception e) {
									}
									if (type.equals("groups"))
										mirrorsGroup.put(key.toLowerCase(), getWorldData(source).getName());

									if (type.equals("users"))
										mirrorsUser.put(key.toLowerCase(), getWorldData(source).getName());
								}

								// Track this world so we can create a datasource for it later
								mirroredWorlds.add(key);

							} else
								GroupManager.logger.log(Level.WARNING, "Mirroring error with " + key + ". Recursive loop detected!");

						} else {
							throw new IllegalStateException("Unknown mirroring format for " + key);
						}

					}
				}
			}

			// Create a datasource for any worlds not already loaded
			for (String world : mirroredWorlds) {
				if (!worldsData.containsKey(world.toLowerCase())) {
					setupWorldFolder(world);
					loadWorld(world, true);
				}
			}
		}
	}

	/**
     *
     */
	public void reloadAll() {

		// Load global groups
		GroupManager.getGlobalGroups().load();

		ArrayList<WorldDataHolder> alreadyDone = new ArrayList<WorldDataHolder>();
		for (WorldDataHolder w : worldsData.values()) {
			if (alreadyDone.contains(w)) {
				continue;
			}
			if (!mirrorsGroup.containsKey(w.getName().toLowerCase()))
				w.reloadGroups();
			if (!mirrorsUser.containsKey(w.getName().toLowerCase()))
				w.reloadUsers();

			alreadyDone.add(w);
		}

	}

	/**
	 * 
	 * @param worldName
	 */
	public void reloadWorld(String worldName) {

		if (!mirrorsGroup.containsKey(worldName.toLowerCase()))
			getWorldData(worldName).reloadGroups();
		if (!mirrorsUser.containsKey(worldName.toLowerCase()))
			getWorldData(worldName).reloadUsers();
	}

	/**
	 * Wrapper to retain backwards compatibility
	 * (call this function to auto overwrite files)
	 */
	public void saveChanges() {

		saveChanges(true);
	}

	/**
     *
     */
	public boolean saveChanges(boolean overwrite) {

		boolean changed = false;
		ArrayList<WorldDataHolder> alreadyDone = new ArrayList<WorldDataHolder>();
		Tasks.removeOldFiles(plugin, plugin.getBackupFolder());

		// Write Global Groups
		if (GroupManager.getGlobalGroups().haveGroupsChanged()) {
			GroupManager.getGlobalGroups().writeGroups(overwrite);
		} else {
			if (GroupManager.getGlobalGroups().getTimeStampGroups() < GroupManager.getGlobalGroups().getGlobalGroupsFile().lastModified()) {
				System.out.print("Newer GlobalGroups file found (Loading changes)!");
				GroupManager.getGlobalGroups().load();
			}
		}

		for (OverloadedWorldHolder w : worldsData.values()) {
			if (alreadyDone.contains(w)) {
				continue;
			}
			if (w == null) {
				GroupManager.logger.severe("WHAT HAPPENED?");
				continue;
			}
			if (!mirrorsGroup.containsKey(w.getName().toLowerCase()))
				if (w.haveGroupsChanged()) {
					if (overwrite || (!overwrite && (w.getTimeStampGroups() >= w.getGroupsFile().lastModified()))) {
						// Backup Groups file
						backupFile(w, true);

						WorldDataHolder.writeGroups(w, w.getGroupsFile());
						changed = true;
						//w.removeGroupsChangedFlag();
					} else {
						// Newer file found.
						GroupManager.logger.log(Level.WARNING, "Newer Groups file found for " + w.getName() + ", but we have local changes!");
						throw new IllegalStateException("Unable to save unless you issue a '/mansave force'");
					}
				} else {
					//Check for newer file as no local changes.
					if (w.getTimeStampGroups() < w.getGroupsFile().lastModified()) {
						System.out.print("Newer Groups file found (Loading changes)!");
						// Backup Groups file
						backupFile(w, true);
						w.reloadGroups();
						changed = true;
					}
				}
			if (!mirrorsUser.containsKey(w.getName().toLowerCase()))
				if (w.haveUsersChanged()) {
					if (overwrite || (!overwrite && (w.getTimeStampUsers() >= w.getUsersFile().lastModified()))) {
						// Backup Users file
						backupFile(w, false);

						WorldDataHolder.writeUsers(w, w.getUsersFile());
						changed = true;
						//w.removeUsersChangedFlag();
					} else {
						// Newer file found.
						GroupManager.logger.log(Level.WARNING, "Newer Users file found for " + w.getName() + ", but we have local changes!");
						throw new IllegalStateException("Unable to save unless you issue a '/mansave force'");
					}
				} else {
					//Check for newer file as no local changes.
					if (w.getTimeStampUsers() < w.getUsersFile().lastModified()) {
						System.out.print("Newer Users file found (Loading changes)!");
						// Backup Users file
						backupFile(w, false);
						w.reloadUsers();
						changed = true;
					}
				}
			alreadyDone.add(w);
		}
		return changed;
	}

	/**
	 * Backup the Groups/Users file
	 * 
	 * @param w
	 * @param groups
	 */
	private void backupFile(OverloadedWorldHolder w, Boolean groups) {

		File backupFile = new File(plugin.getBackupFolder(), "bkp_" + w.getName() + (groups ? "_g_" : "_u_") + Tasks.getDateString() + ".yml");
		try {
			Tasks.copy((groups ? w.getGroupsFile() : w.getUsersFile()), backupFile);
		} catch (IOException ex) {
			GroupManager.logger.log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Returns the dataHolder for the given world.
	 * If the world is not on the worlds list, returns the default world
	 * holder.
	 * 
	 * Mirrors return their parent world data.
	 * If no mirroring data it returns the default world.
	 * 
	 * @param worldName
	 * @return OverloadedWorldHolder
	 */
	public OverloadedWorldHolder getWorldData(String worldName) {

		String worldNameLowered = worldName.toLowerCase();

		// Find this worlds data
		if (worldsData.containsKey(worldNameLowered))
			return getUpdatedWorldData(worldNameLowered);

		// Oddly no data source was found for this world so return the default.
		GroupManager.logger.finest("Requested world " + worldName + " not found or badly mirrored. Returning default world...");
		return getDefaultWorld();
	}

	/**
	 * Get the requested world data and update it's dataSource to be relevant
	 * for this world
	 * 
	 * @param worldName
	 * @return updated world holder
	 */
	private OverloadedWorldHolder getUpdatedWorldData(String worldName) {

		String worldNameLowered = worldName.toLowerCase();

		if (worldsData.containsKey(worldNameLowered)) {
			OverloadedWorldHolder data = worldsData.get(worldNameLowered);
			data.updateDataSource();
			return data;
		}
		return null;

	}

	/**
	 * Do a matching of playerName, if its found only one player, do
	 * getWorldData(player)
	 * 
	 * @param playerName
	 * @return null if matching returned no player, or more than one.
	 */
	public OverloadedWorldHolder getWorldDataByPlayerName(String playerName) {

		List<Player> matchPlayer = plugin.getServer().matchPlayer(playerName);
		if (matchPlayer.size() == 1) {
			return getWorldData(matchPlayer.get(0));
		}
		return null;
	}

	/**
	 * Retrieves the field player.getWorld().getName() and do
	 * getWorld(worldName)
	 * 
	 * @param player
	 * @return OverloadedWorldHolder
	 */
	public OverloadedWorldHolder getWorldData(Player player) {

		return getWorldData(player.getWorld().getName());
	}

	/**
	 * It does getWorld(worldName).getPermissionsHandler()
	 * 
	 * @param worldName
	 * @return AnjoPermissionsHandler
	 */
	public AnjoPermissionsHandler getWorldPermissions(String worldName) {

		return getWorldData(worldName).getPermissionsHandler();
	}

	/**
	 * Returns the PermissionsHandler for this player data
	 * 
	 * @param player
	 * @return AnjoPermissionsHandler
	 */
	public AnjoPermissionsHandler getWorldPermissions(Player player) {

		return getWorldData(player).getPermissionsHandler();
	}

	/**
	 * Id does getWorldDataByPlayerName(playerName).
	 * If it doesnt return null, it will return result.getPermissionsHandler()
	 * 
	 * @param playerName
	 * @return null if the player matching gone wrong.
	 */
	public AnjoPermissionsHandler getWorldPermissionsByPlayerName(String playerName) {

		WorldDataHolder dh = getWorldDataByPlayerName(playerName);
		if (dh != null) {
			return dh.getPermissionsHandler();
		}
		return null;
	}

	private void verifyFirstRun() {

		Properties server = new Properties();
		try {
			server.load(new FileInputStream(new File("server.properties")));
			serverDefaultWorldName = server.getProperty("level-name").toLowerCase();
			setupWorldFolder(serverDefaultWorldName);
		} catch (IOException ex) {
			GroupManager.logger.log(Level.SEVERE, null, ex);
		}

	}

	public void setupWorldFolder(String worldName) {

		String worldNameLowered = worldName.toLowerCase();

		worldsFolder = new File(plugin.getDataFolder(), "worlds");
		if (!worldsFolder.exists()) {
			worldsFolder.mkdirs();
		}

		File defaultWorldFolder = new File(worldsFolder, worldNameLowered);
		if ((!defaultWorldFolder.exists()) && ((!mirrorsGroup.containsKey(worldNameLowered))) || (!mirrorsUser.containsKey(worldNameLowered))) {

			/*
			 * check and convert all old case sensitive folders to lower case
			 */
			File casedWorldFolder = new File(worldsFolder, worldName);
			if ((casedWorldFolder.exists()) && (casedWorldFolder.getName().toLowerCase().equals(worldNameLowered))) {
				/*
				 * Rename the old folder to the new lower cased format
				 */
				casedWorldFolder.renameTo(new File(worldsFolder, worldNameLowered));
			} else {
				/*
				 * Else we just create the folder
				 */
				defaultWorldFolder.mkdirs();
			}
		}
		if (defaultWorldFolder.exists()) {
			if (!mirrorsGroup.containsKey(worldNameLowered)) {
				File groupsFile = new File(defaultWorldFolder, "groups.yml");
				if (!groupsFile.exists() || groupsFile.length() == 0) {

					InputStream template = plugin.getResourceAsStream("groups.yml");
					try {
						Tasks.copy(template, groupsFile);
					} catch (IOException ex) {
						GroupManager.logger.log(Level.SEVERE, null, ex);
					}
				}
			}

			if (!mirrorsUser.containsKey(worldNameLowered)) {
				File usersFile = new File(defaultWorldFolder, "users.yml");
				if (!usersFile.exists() || usersFile.length() == 0) {

					InputStream template = plugin.getResourceAsStream("users.yml");
					try {
						Tasks.copy(template, usersFile);
					} catch (IOException ex) {
						GroupManager.logger.log(Level.SEVERE, null, ex);
					}

				}
			}
		}
	}

	/**
	 * Copies the specified world data to another world
	 * 
	 * @param fromWorld
	 * @param toWorld
	 * @return true if successfully copied.
	 */
	public boolean cloneWorld(String fromWorld, String toWorld) {

		File fromWorldFolder = new File(worldsFolder, fromWorld.toLowerCase());
		File toWorldFolder = new File(worldsFolder, toWorld.toLowerCase());
		if (toWorldFolder.exists() || !fromWorldFolder.exists()) {
			return false;
		}
		File fromWorldGroups = new File(fromWorldFolder, "groups.yml");
		File fromWorldUsers = new File(fromWorldFolder, "users.yml");
		if (!fromWorldGroups.exists() || !fromWorldUsers.exists()) {
			return false;
		}
		File toWorldGroups = new File(toWorldFolder, "groups.yml");
		File toWorldUsers = new File(toWorldFolder, "users.yml");
		toWorldFolder.mkdirs();
		try {
			Tasks.copy(fromWorldGroups, toWorldGroups);
			Tasks.copy(fromWorldUsers, toWorldUsers);
		} catch (IOException ex) {
			Logger.getLogger(WorldsHolder.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
		return true;
	}

	/**
	 * Wrapper for LoadWorld(String,Boolean) for backwards compatibility
	 * 
	 * Load a world from file.
	 * If it already been loaded, summon reload method from dataHolder.
	 * 
	 * @param worldName
	 */
	public void loadWorld(String worldName) {

		loadWorld(worldName, false);
	}

	/**
	 * Load a world from file.
	 * If it already been loaded, summon reload method from dataHolder.
	 * 
	 * @param worldName
	 */
	public void loadWorld(String worldName, Boolean isMirror) {

		String worldNameLowered = worldName.toLowerCase();

		if (worldsData.containsKey(worldNameLowered)) {
			worldsData.get(worldNameLowered).reload();
			return;
		}
		GroupManager.logger.finest("Trying to load world " + worldName + "...");
		File thisWorldFolder = new File(worldsFolder, worldNameLowered);
		if ((isMirror) || (thisWorldFolder.exists() && thisWorldFolder.isDirectory())) {

			// Setup file handles, if not mirrored
			File groupsFile = (mirrorsGroup.containsKey(worldNameLowered)) ? null : new File(thisWorldFolder, "groups.yml");
			File usersFile = (mirrorsUser.containsKey(worldNameLowered)) ? null : new File(thisWorldFolder, "users.yml");

			if ((groupsFile != null) && (!groupsFile.exists())) {
				throw new IllegalArgumentException("Groups file for world '" + worldName + "' doesnt exist: " + groupsFile.getPath());
			}
			if ((usersFile != null) && (!usersFile.exists())) {
				throw new IllegalArgumentException("Users file for world '" + worldName + "' doesnt exist: " + usersFile.getPath());
			}

			WorldDataHolder tempHolder = new WorldDataHolder(worldName);

			// Map the group object for any mirror
			if (mirrorsGroup.containsKey(worldNameLowered))
				tempHolder.setGroupsObject(this.getWorldData(mirrorsGroup.get(worldNameLowered)).getGroupsObject());
			else
				tempHolder.loadGroups(groupsFile);

			// Map the user object for any mirror
			if (mirrorsUser.containsKey(worldNameLowered))
				tempHolder.setUsersObject(this.getWorldData(mirrorsUser.get(worldNameLowered)).getUsersObject());
			else
				tempHolder.loadUsers(usersFile);

			OverloadedWorldHolder thisWorldData = new OverloadedWorldHolder(tempHolder);

			// null the object so we don't keep file handles open where we shouldn't
			tempHolder = null;

			// Set the file TimeStamps as it will be default from the initial load.
			thisWorldData.setTimeStamps();

			if (thisWorldData != null) {
				GroupManager.logger.finest("Successful load of world " + worldName + "...");
				worldsData.put(worldNameLowered, thisWorldData);
				return;
			}

			//GroupManager.logger.severe("Failed to load world " + worldName + "...");
		}
	}

	/**
	 * Tells if the such world has been mapped.
	 * 
	 * It will return true if world is a mirror.
	 * 
	 * @param worldName
	 * @return true if world is loaded or mirrored. false if not listed
	 */
	public boolean isInList(String worldName) {

		if (worldsData.containsKey(worldName.toLowerCase()) || mirrorsGroup.containsKey(worldName.toLowerCase()) || mirrorsUser.containsKey(worldName.toLowerCase())) {
			return true;
		}
		return false;
	}

	/**
	 * Verify if world has it's own file permissions.
	 * 
	 * @param worldName
	 * @return true if it has its own holder. false if not.
	 */
	public boolean hasOwnData(String worldName) {

		if (worldsData.containsKey(worldName.toLowerCase()) && (!mirrorsGroup.containsKey(worldName.toLowerCase()) || !mirrorsUser.containsKey(worldName.toLowerCase()))) {
			return true;
		}
		return false;
	}

	/**
	 * @return the defaultWorld
	 */
	public OverloadedWorldHolder getDefaultWorld() {

		return getUpdatedWorldData(serverDefaultWorldName);
	}

	/**
	 * Returns all physically loaded worlds which have at least
	 * one of their own data sets for users or groups.
	 * 
	 * @return ArrayList<OverloadedWorldHolder> of all loaded worlds
	 */
	public ArrayList<OverloadedWorldHolder> allWorldsDataList() {

		ArrayList<OverloadedWorldHolder> list = new ArrayList<OverloadedWorldHolder>();
		for (OverloadedWorldHolder data : worldsData.values()) {
			if ((!list.contains(data)) && (!mirrorsGroup.containsKey(data.getName().toLowerCase()) || !mirrorsUser.containsKey(data.getName().toLowerCase()))) {

				String worldNameLowered = data.getName().toLowerCase();
				String usersMirror = mirrorsUser.get(worldNameLowered);
				String groupsMirror = mirrorsGroup.get(worldNameLowered);

				// is users mirrored?
				if (usersMirror != null) {

					// If both are mirrored
					if (groupsMirror != null) {

						// if the data sources are the same, return the parent
						if (usersMirror == groupsMirror) {
							if (!list.contains(usersMirror.toLowerCase()))
								list.add(worldsData.get(usersMirror.toLowerCase()));
							continue;
						}
						// Both data sources are mirrors, but they are from different parents
						// so fall through to add the actual data object.
					}
					// Groups isn't a mirror so fall through to add this this worlds data source
				}

				// users isn't mirrored so we need to add this worlds data source
				list.add(data);
			}
		}
		return list;
	}
}
