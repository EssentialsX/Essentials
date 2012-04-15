package org.anjocaido.groupmanager.permissions;

//import java.util.Collection;
//import java.util.Map;
//import java.util.Set;
import java.util.List;
import java.util.Set;

import org.anjocaido.groupmanager.data.Group;
//import org.anjocaido.groupmanager.data.User;
import org.bukkit.entity.Player;

/**
 * Made by Nijikokun. Changed by Gabriel Couto
 * 
 * This class is intended to *read* permissions from a single world.
 * 
 * @author Nijikokun
 * @author Gabriel Couto
 * @author ElgarL
 */
public abstract class PermissionsReaderInterface {

	/**
	 * 
	 * @param player
	 * @param string
	 * @return true if has permission
	 */
	public abstract boolean has(Player player, String string);

	/**
	 * 
	 * @param player
	 * @param string
	 * @return true if has permission
	 */
	public abstract boolean permission(Player player, String string);

	/**
	 * 
	 * @param userName
	 * @return group name for this player.
	 */
	public abstract String getGroup(String userName);

	/**
	 * 
	 * @param userName
	 * @param groupName
	 * @return true if in group
	 */
	public abstract boolean inGroup(String userName, String groupName);

	/**
	 * 
	 * @param groupName
	 * @return String of prefix
	 */
	public abstract String getGroupPrefix(String groupName);

	/**
	 * 
	 * @param groupName
	 * @return String of suffix
	 */
	public abstract String getGroupSuffix(String groupName);

	/**
	 * 
	 * @param groupName
	 * @return true if can build
	 */
	public abstract boolean canGroupBuild(String groupName);

	/**
	 * 
	 * @param groupName
	 * @param node
	 * @return String value
	 */
	public abstract String getGroupPermissionString(String groupName, String node);

	/**
	 * 
	 * @param groupName
	 * @param node
	 * @return integer value
	 */
	public abstract int getGroupPermissionInteger(String groupName, String node);

	/**
	 * 
	 * @param groupName
	 * @param node
	 * @return boolean value
	 */
	public abstract boolean getGroupPermissionBoolean(String groupName, String node);

	/**
	 * 
	 * @param groupName
	 * @param node
	 * @return double value
	 */
	public abstract double getGroupPermissionDouble(String groupName, String node);

	/**
	 * 
	 * @param userName
	 * @param node
	 * @return String value
	 */
	public abstract String getUserPermissionString(String userName, String node);

	/**
	 * 
	 * @param userName
	 * @param node
	 * @return integer value
	 */
	public abstract int getUserPermissionInteger(String userName, String node);

	/**
	 * 
	 * @param userName
	 * @param node
	 * @return boolean value
	 */
	public abstract boolean getUserPermissionBoolean(String userName, String node);

	/**
	 * 
	 * @param userName
	 * @param node
	 * @return double value
	 */
	public abstract double getUserPermissionDouble(String userName, String node);

	/**
	 * 
	 * @param userName
	 * @param node
	 * @return String value
	 */
	public abstract String getPermissionString(String userName, String node);

	/**
	 * 
	 * @param userName
	 * @param node
	 * @return integer value
	 */
	public abstract int getPermissionInteger(String userName, String node);

	/**
	 * 
	 * @param userName
	 * @param node
	 * @return boolean value
	 */
	public abstract boolean getPermissionBoolean(String userName, String node);

	/**
	 * 
	 * @param userName
	 * @param node
	 * @return double value
	 */
	public abstract double getPermissionDouble(String userName, String node);

	/////////////////////////////
	/**
	 * Gets the appropriate prefix for the user.
	 * This method is a utility method for chat plugins to get the user's prefix
	 * without having to look at every one of the user's ancestors.
	 * Returns an empty string if user has no parent groups.
	 * 
	 * @param user Player's name
	 * @return Player's prefix
	 */
	public abstract String getUserPrefix(String user);

	/**
	 * Gets the appropriate suffix for the user.
	 * This method is a utility method for chat plugins to get the user's suffix
	 * without having to look at every one of the user's ancestors.
	 * Returns an empty string if user has no parent groups.
	 * 
	 * @param user Player's name
	 * @return Player's suffix
	 */
	public abstract String getUserSuffix(String user);

	/**
	 * Returns the group object representing the default group of the given
	 * world.
	 * This method will return null if the object does not exist or the world
	 * has no default group.
	 * 
	 * @return Group object representing default world, or null if it doesn't
	 *         exist or is not defined.
	 */
	public abstract Group getDefaultGroup();

	/**
	 * Gets a array of the names of all parent groups in the same world.
	 * 
	 * @param name Target user's name
	 * @return An array containing the names of all parent groups (including
	 *         ancestors) that are in the same world
	 */
	public abstract String[] getGroups(String name);

	public abstract String getInfoString(String entryName, String path, boolean isGroup);

	//public abstract String getInfoString(String entryName, String path, boolean isGroup, Comparator<String> comparator);

	public abstract int getInfoInteger(String entryName, String path, boolean isGroup);

	//public abstract int getInfoInteger(String entryName, String path, boolean isGroup, Comparator<Integer> comparator);

	/**
	 * Gets a double from the Info node without inheritance.
	 * 
	 * @param entryName
	 * @param path
	 * @param isGroup
	 * @return -1 if not found
	 */
	public abstract double getInfoDouble(String entryName, String path, boolean isGroup);

	//public abstract double getInfoDouble(String entryName, String path, boolean isGroup, Comparator<Double> comparator);

	public abstract boolean getInfoBoolean(String entryName, String path, boolean isGroup);

	//public abstract boolean getInfoBoolean(String entryName, String path, boolean isGroup, Comparator<Boolean> comparator);

	public abstract void addUserInfo(String name, String path, Object data);

	public abstract void removeUserInfo(String name, String path);

	public abstract void addGroupInfo(String name, String path, Object data);

	public abstract void removeGroupInfo(String name, String path);

	//////////////////////////////

	public abstract List<String> getAllPlayersPermissions(String userName);

	public abstract Set<String> getAllPlayersPermissions(String userName, Boolean includeChildren);
}
