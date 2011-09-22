package com.nijiko.permissions;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

/**
 * Permissions 3.0
 * Copyright (C) 2011  Matt 'The Yeti' Burnett <admin@theyeticave.net>
 * Original Credit & Copyright (C) 2010 Nijikokun <nijikokun@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Permissions Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Permissions Public License for more details.
 *
 * You should have received a copy of the GNU Permissions Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public abstract class PermissionHandler {
    //World config manipulation methods
    
    /**
     * Sets the default world that is loaded on startup.
     * @param world World to load on startup
     */
    public abstract void setDefaultWorld(String world);
    /**
     * Check if world is loaded.
     * A world is considered as loaded if there exists a user/group storage for it or it mirrors another world.
     * @param world Target world
     * @return Whether the world is loaded.
     */
    public abstract boolean checkWorld(String world);
    
    /**
     * Attempts to load a world. If the world is already loaded, nothing happens.
     * @param world World to load
     * @return Whether world isn't already loaded
     * @throws Exception Any exception that may occur when loading the world.
     */
    public abstract boolean loadWorld(String world) throws Exception;
    /**
     * Forces a load of the world.
     * @param world World to load
     * @throws Exception Any exception that may occur when loading the world.
     */
    public abstract void forceLoadWorld(String world) throws Exception;
    /**
     * Returns a set of the names all loaded worlds. 
     * A world is considered as loaded if there exists a user/group storage for it or it mirrors another world.
     * @return Set of the names all loaded worlds. 
     */
    public abstract Set<String> getWorlds();
    /**
     * Loads the default world and global permissions.
     * @throws Exception  Any exception that may occur when loading the worlds.
     */
    public abstract void load() throws Exception;
    /**
     * Reloads all worlds
     */
    public abstract void reload();
    /**
     * Reloads the specified world
     * @param world Name of target world
     * @return False if world is not loaded, true otherwise
     */
    public abstract boolean reload(String world);
    /**
     * Forces all storages to save,
     */
    public abstract void saveAll();
    /**
     * This method forces a save of the specified world
     * @param world Name of target world
     */
    public abstract void save(String world);
    /**
     * Forces all storages to close.
     */
    public abstract void closeAll();

    //Permission-checking methods
    /**
     * This is an alias for permission(Player, String).
     * @param player Target player
     * @param node Permission node
     * @return True if the player has the specified node, false otherwise
     */
    public abstract boolean has(Player player, String node);
    /**
     * This is an alias for permission(String, String, String).
     * @param worldName Target world (may be different from player's current world)
     * @param playerName Player's name
     * @param node Permission node
     * @return True if the player has the specified node, false otherwise
     */
    public abstract boolean has(String worldName, String playerName, String node);
    /**
     * This method checks if the player has the given permissions in his/her current world.
     * In other words, this calls permissions(player.getWorld().getName(), player.getName(), node).
     * @param player Target player
     * @param node Permission node
     * @return True if the player has the specified node, false otherwise
     */
    public abstract boolean permission(Player player, String node);
    /**
     * This method checks if the player has the given permissions in the given world.
     * @param worldName Target world (may be different from player's current world)
     * @param playerName Player's name
     * @param node Permission node
     * @return True if the player has the specified node, false otherwise
     */
    public abstract boolean permission(String worldName, String playerName, String node);

    //Permission-manipulation methods
    /**
     * Adds the given permission to the targeted player.
     * @param world Target world (may be different from player's current world)
     * @param user Player's name
     * @param node Permission node
     */
    public abstract void addUserPermission(String world, String user, String node);
    /**
     * Removes the given permission from the targeted player.
     * @param world Target world (may be different from player's current world)
     * @param user Player's name
     * @param node Permission node
     */
    public abstract void removeUserPermission(String world, String user, String node);
    /**
     * Adds the given permission to the targeted group.
     * @param world Target world
     * @param user Group's name
     * @param node Permission node
     */
    public abstract void addGroupPermission(String world, String user, String node);
    /**
     * Removes the given permission from the targeted group.
     * @param world Target world
     * @param user Group's name
     * @param node Permission node
     */
    public abstract void removeGroupPermission(String world, String user, String node);

    //Chat, prefix, suffix, build methods
    /**
     * Retrieves the properly-capitalised version of the given group's name.
     * Returns an empty string if group does not exist.
     * @param world Group's world
     * @param group Group's name (any capitalisation)
     * @return Group's properly-capitalised name.
     */
    public abstract String getGroupProperName(String world, String group);
    /**
     * Gets the appropriate prefix for the user.
     * This method is a utility method for chat plugins to get the user's prefix 
     * without having to look at every one of the user's ancestors.
     * Returns an empty string if user has no parent groups.
     * @param world Player's world
     * @param user Player's name
     * @return Player's prefix
     */
    public abstract String getUserPrefix(String world, String user);
    /**
     * Gets the appropriate suffix for the user.
     * This method is a utility method for chat plugins to get the user's suffix 
     * without having to look at every one of the user's ancestors.
     * Returns an empty string if user has no parent groups.
     * @param world Player's world
     * @param user Player's name
     * @return Player's suffix
     */
    public abstract String getUserSuffix(String world, String user);
    /**
     * Gets the primary group of the user.
     * Returns the default group if user has no parent groups,
     * or null if there is no default group for that world.
     * @param world Player's world
     * @param user Player's name
     * @return Player's primary group
     */
    public abstract Group getPrimaryGroupObject(String world, String user);
    /**
     * Gets name of the primary group of the user.
     * Returns the name of the default group if user has no parent groups,
     * or "Default" if there is no default group for that world.
     * @param world Player's world
     * @param user Player's name
     * @return Name of player's primary group
     */
    public abstract String getPrimaryGroup(String world, String user);
    /**
     * Check if user can build.
     * @param world Player's world
     * @param user Player's name
     * @return Whether the user can build
     */
    public abstract boolean canUserBuild(String world, String user);
    
    /**
     * Retrieves group's raw prefix, inheritance not included.
     * Will return an empty string if no prefix is defined for the group.
     * @param world Group's world
     * @param group Group's name
     * @return The prefix defined for the group, empty string if prefix is not defined.
     */
    public abstract String getGroupRawPrefix(String world, String group);
    /**
     * Retrieves group's raw suffix, inheritance not included.
     * Will return an empty string if no suffix is defined for the group.
     * @param world Group's world
     * @param group Group's name
     * @return The suffix defined for the group, empty string if suffix is not defined.
     */
    public abstract String getGroupRawSuffix(String world, String group);
    /**
     * Retrieves group's build setting, inheritance not included.
     * @param world Group's world
     * @param group Group's name
     * @return Returns group's build setting.
     */
    public abstract boolean canGroupRawBuild(String world, String group);

    //Entry methods
    /**
     * Returns the user object defined by the given world and name.
     * This method attempts to create the object if it does not exist.
     * @param world User's world
     * @param name User's name
     * @return User object
     * @throws Exception An exception when creating the user object
     */
    public abstract User safeGetUser(String world, String name) throws Exception;
    /**
     * Returns the group object defined by the given world and name.
     * This method attempts to create the object if it does not exist.
     * @param world Group's world
     * @param name Group's name
     * @return Group object
     * @throws Exception An exception when creating the group object
     */
    public abstract Group safeGetGroup(String world, String name) throws Exception;
    /**
     * Returns the user object defined by the given world and name.
     * This method will return null if the object does not exist.
     * @param world User's world
     * @param name User's name
     * @return User object, or null if it doesn't exist
     */
    public abstract User getUserObject(String world, String name);
    /**
     * Returns the group object defined by the given world and name.
     * This method will return null if the object does not exist.
     * @param world Group's world
     * @param name Group's name
     * @return Group object, or null if it doesn't exist
     */
    public abstract Group getGroupObject(String world, String name);

    /**
     * Returns the group object representing the default group of the given world.
     * This method will return null if the object does not exist or the world has no default group.
     * @param world Target world
     * @return Group object representing default world, or null if it doesn't exist or is not defined.
     */
    public abstract Group getDefaultGroup(String world);
    /**
     * Returns all the user objects in the world.
     * Will return null if world does not exist.
     * @param world Target world
     * @return Collection of all user objects belonging to the world.
     */
    public abstract Collection<User> getUsers(String world);
    /**
     * Returns all the group objects in the world.
     * Will return null if world does not exist.
     * @param world Target world
     * @return Collection of all group objects belonging to the world.
     */
    public abstract Collection<Group> getGroups(String world);

    //Parent-related methods
    public abstract Set<String> getTracks(String world);
    /**
     * Checks if user is in specified group. Includes inherited groups.
     * @param world World of both the user and the group
     * @param user User's name
     * @param group Parent group's name
     * @return Whether user is a child of the specified group
     */
    public abstract boolean inGroup(String world, String user, String group);
    /**
     * Checks if user is in specified group. Includes inherited groups.
     * @param world User's world
     * @param user User's name
     * @param groupWorld Parent group's world
     * @param group Parent group's name
     * @return Whether user is a child of the specified group
     */
    public abstract boolean inGroup(String world, String user, String groupWorld, String group);
    /**
     * Checks if user is in specified group, not counting inherited parents.
     * @param world World of both the user and the group
     * @param user User's name
     * @param group Parent group's name
     * @return Whether user is a child of the specified group
     */
    public abstract boolean inSingleGroup(String world, String user, String group);
    /**
     * Checks if user is in specified group, not counting inherited parents.
     * @param world User's world
     * @param user User's name
     * @param groupWorld Parent group's world
     * @param group Parent group's name
     * @return Whether user is a child of the specified group
     */
    public abstract boolean inSingleGroup(String world, String user, String groupWorld, String group);
    /**
     * Gets a array of the names of all parent groups in the same world. 
     * @param world Target user's world
     * @param name Target user's name
     * @return An array containing the names of all parent groups (including ancestors) that are in the same world
     */
    public abstract String[] getGroups(String world, String name);
    /**
     * Gets a map of world name to all parent groups of the target user in that world. 
     * @param world Target user's world
     * @param name Target user's name
     * @return Map of world name to set of groups that the user inherits from in the world.
     */
    public abstract Map<String, Set<String>> getAllGroups(String world, String name);

    //Weight-related methods
    /**
     * Compare the weights of two users.
     * This method is for plugin devs to compare whether a user can do an action to another user.
     * For example, SlapPlugin can compare the weights of two users when one of them wants to /slap the other.
     * It can decide whether to allow the slap using the result of this function.
     * @param firstWorld First user's world
     * @param first First user's name
     * @param secondWorld Second user's world
     * @param second Second user's name
     * @return -1 if firstWeight < secondWeight, 0 if firstWeight == secondWeight, 1 if firstWeight > secondWeight 
     */
    public abstract int compareWeights(String firstWorld, String first, String secondWorld, String second);
    /**
     * Alias for compareWeights(world, first, world, second).
     * @param world World
     * @param first First user's name
     * @param second Second user's name
     * @return -1 if firstWeight < secondWeight, 0 if firstWeight == secondWeight, 1 if firstWeight > secondWeight 
     */
    public abstract int compareWeights(String world, String first, String second);
    
    //Data-related methods
    public abstract String getRawInfoString(String world, String entryName, String path,boolean isGroup);
    
    public abstract Integer getRawInfoInteger(String world, String entryName, String path, boolean isGroup);
    
    public abstract Double getRawInfoDouble(String world, String entryName, String path, boolean isGroup);
    
    public abstract Boolean getRawInfoBoolean(String world, String entryName, String path, boolean isGroup);


    public abstract String getInfoString(String world, String entryName, String path,boolean isGroup);
    public abstract String getInfoString(String world, String entryName, String path, boolean isGroup, Comparator<String> comparator);
    
    public abstract Integer getInfoInteger(String world, String entryName, String path, boolean isGroup);
    public abstract Integer getInfoInteger(String world, String entryName, String path, boolean isGroup, Comparator<Integer> comparator);
    
    public abstract Double getInfoDouble(String world, String entryName, String path, boolean isGroup);
    public abstract Double getInfoDouble(String world, String entryName, String path, boolean isGroup, Comparator<Double> comparator);
    
    public abstract Boolean getInfoBoolean(String world, String entryName, String path, boolean isGroup);
    public abstract Boolean getInfoBoolean(String world, String entryName, String path, boolean isGroup, Comparator<Boolean> comparator);
    
    
    public abstract void addUserInfo(String world, String name, String path, Object data);
    public abstract void removeUserInfo(String world, String name, String path);
    public abstract void addGroupInfo(String world, String name, String path, Object data);
    public abstract void removeGroupInfo(String world, String name, String path);
    
    //Legacy methods
    @Deprecated
    public abstract String getGroupPermissionString(String world, String group, String path);
    @Deprecated
    public abstract int getGroupPermissionInteger(String world, String group, String path);
    @Deprecated
    public abstract boolean getGroupPermissionBoolean(String world, String group, String path);
    @Deprecated
    public abstract double getGroupPermissionDouble(String world, String group, String path);
    
    @Deprecated
    public abstract String getUserPermissionString(String world, String group, String path);
    @Deprecated
    public abstract int getUserPermissionInteger(String world, String group, String path);
    @Deprecated
    public abstract boolean getUserPermissionBoolean(String world, String group, String path);
    @Deprecated
    public abstract double getUserPermissionDouble(String world, String group, String path);
    
    @Deprecated
    public abstract String getPermissionString(String world, String group, String path);
    @Deprecated
    public abstract int getPermissionInteger(String world, String group, String path);
    @Deprecated
    public abstract boolean getPermissionBoolean(String world, String group, String path);
    @Deprecated
    public abstract double getPermissionDouble(String world, String group, String path);
    

    @Deprecated
    public abstract String getGroup(String world, String group);

    @Deprecated
    public abstract String getGroupPrefix(String world, String group);
    @Deprecated
    public abstract String getGroupSuffix(String world, String group);
    @Deprecated
    public abstract boolean canGroupBuild(String world, String group);
    
    //Cache methods are no longer available
//    @Deprecated
//    public void setCache(String world, Map<String, Boolean> Cache) {
//    }
//    @Deprecated
//    public void setCacheItem(String world, String player, String permission, boolean data){
//    }
//    @Deprecated
//    public Map<String, Boolean> getCache(String world){
//        return null;
//    }
//    @Deprecated
//    public boolean getCacheItem(String world, String player, String permission){
//        return false;
//    }
//    @Deprecated
//    public void removeCachedItem(String world, String player, String permission){
//    }
//    @Deprecated
//    public void clearCache(String world){
//    }
//    @Deprecated
//    public void clearAllCache(){
//    }

}