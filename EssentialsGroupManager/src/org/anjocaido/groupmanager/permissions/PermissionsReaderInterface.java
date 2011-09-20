package org.anjocaido.groupmanager.permissions;

//import java.util.Collection;
//import java.util.Map;
//import java.util.Set;
import java.util.List;

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
 */
public abstract class PermissionsReaderInterface {

    /**
     *
     * @param player
     * @param string
     * @return
     */
    public abstract boolean has(Player player, String string);

    /**
     *
     * @param player
     * @param string
     * @return
     */
    public abstract boolean permission(Player player, String string);

    /**
     *
     * @param string
     * @return
     */
    public abstract String getGroup(String string);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract boolean inGroup(String string, String string1);

    /**
     *
     * @param string
     * @return
     */
    public abstract String getGroupPrefix(String string);

    /**
     *
     * @param string
     * @return
     */
    public abstract String getGroupSuffix(String string);

    /**
     *
     * @param string
     * @return
     */
    public abstract boolean canGroupBuild(String string);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract String getGroupPermissionString(String string, String string1);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract int getGroupPermissionInteger(String string, String string1);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract boolean getGroupPermissionBoolean(String string, String string1);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract double getGroupPermissionDouble(String string, String string1);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract String getUserPermissionString(String string, String string1);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract int getUserPermissionInteger(String string, String string1);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract boolean getUserPermissionBoolean(String string, String string1);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract double getUserPermissionDouble(String string, String string1);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract String getPermissionString(String string, String string1);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract int getPermissionInteger(String string, String string1);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract boolean getPermissionBoolean(String string, String string1);

    /**
     *
     * @param string
     * @param string1
     * @return
     */
    public abstract double getPermissionDouble(String string, String string1);

/////////////////////////////
    /**
     * Gets the appropriate prefix for the user.
     * This method is a utility method for chat plugins to get the user's prefix
     * without having to look at every one of the user's ancestors.
     * Returns an empty string if user has no parent groups.
     * @param world Player's world
     * @param user Player's name
     * @return Player's prefix
     */
    public abstract String getUserPrefix(String user);

    /**
     * Gets the appropriate suffix for the user.
     * This method is a utility method for chat plugins to get the user's suffix
     * without having to look at every one of the user's ancestors.
     * Returns an empty string if user has no parent groups.
     * @param world Player's world
     * @param user Player's name
     * @return Player's suffix
     */
    public abstract String getUserSuffix(String user);

    /**
     * Returns the group object representing the default group of the given world.
     * This method will return null if the object does not exist or the world has no default group.
     * @return Group object representing default world, or null if it doesn't exist or is not defined.
     */
    public abstract Group getDefaultGroup();

    /**
     * Gets a array of the names of all parent groups in the same world.
     * @param name Target user's name
     * @return An array containing the names of all parent groups (including ancestors) that are in the same world
     */
    public abstract String[] getGroups(String name);

    public abstract String getInfoString(String entryName, String path, boolean isGroup);
    //public abstract String getInfoString(String entryName, String path, boolean isGroup, Comparator<String> comparator);

    public abstract int getInfoInteger(String entryName, String path, boolean isGroup);
    //public abstract int getInfoInteger(String entryName, String path, boolean isGroup, Comparator<Integer> comparator);

    /**
     * Gets a double from the Info node without inheritance.
     * @param entryName
     * @param path
     * @param isGroup
     * @return
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
}
