/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.permissions;

import com.nijiko.permissions.Group;
import com.nijiko.permissions.PermissionHandler;
import com.nijiko.permissions.User;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Everything here maintains the model created by Nijikokun
 *
 * But implemented to use GroupManager system. Which provides instant changes,
 * without file access.
 *
 * @author gabrielcouto
 */
public class NijikoPermissionsProxy extends PermissionHandler {

    GroupManager plugin;

    public NijikoPermissionsProxy(GroupManager plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public void addGroupPermission(String world, String group, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeGroupPermission(String world, String group, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addGroupInfo(String world, String group, String node, Object data) {
        plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().addGroupInfo(group, node, data);
    }

    @Override
    public void removeGroupInfo(String world, String group, String node) {
        plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().removeGroupInfo(group, node);
    }

    @Override
    public void addUserPermission(String world, String user, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeUserPermission(String world, String user, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addUserInfo(String world, String user, String node, Object data) {
        plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().addUserInfo(user, node, data);
    }

    @Override
    public void removeUserInfo(String world, String user, String node) {
        plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().removeUserInfo(user, node);
    }

    @Deprecated
    public void removeUserInfo(String user, String node) {
        plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().removeUserInfo(user, node);
    }

    @Deprecated
    public void addGroupPermission(String group, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Deprecated
    public void removeGroupPermission(String group, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Deprecated
    public void addGroupInfo(String group, String node, Object data) {
        plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().addGroupInfo(group, node, data);
    }

    @Deprecated
    public void removeGroupInfo(String group, String node) {
        plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().removeGroupInfo(group, node);
    }

    @Deprecated
    public void addUserPermission(String user, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Deprecated
    public void removeUserPermission(String user, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Deprecated
    public void addUserInfo(String user, String node, Object data) {
        plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().addUserInfo(user, node, data);
    }

    @Override
    public void setDefaultWorld(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Deprecated
    public void setDirectory(File directory) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean loadWorld(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void forceLoadWorld(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean checkWorld(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void load() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reload() {
        plugin.getWorldsHolder().reloadAll();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean reload(String world) {
        plugin.getWorldsHolder().reloadWorld(world);
        return true;
    }

    @Override
    public boolean has(Player player, String permission) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return permission(player, permission);
    }

    @Override
    public boolean permission(Player player, String permission) {
        //throw new UnsupportedOperationException("Not supported yet.");
        if (permission == null || permission.equals("")) {
            return false;
        }
        if (player == null) {
            GroupManager.logger.severe("A plugin is asking permission '" + permission + "' for a null player... Which plugin does that? Bastards!");
            return false;
        }
        if (player.getWorld() == null) {
            GroupManager.logger.warning("The player " + player.getName() + " has a null world? Treating as default world!");
            return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().has(player, permission);
        }
        return plugin.getWorldsHolder().getWorldData(player.getWorld().getName()).getPermissionsHandler().has(player, permission);
    }

    @Override
    public String getGroupRawPrefix(String world, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupPrefix(group);
    }

    @Override
    public String getGroupRawSuffix(String world, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupSuffix(group);
    }

    @Override
    public String getUserPrefix(String world, String user) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getUserPrefix(user);
    }

    @Override
    public String getUserSuffix(String world, String user) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getUserSuffix(user);
    }

    @Override
    public String getGroup(String world, String user) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroup(user);
    }

    @Override
    public String getPrimaryGroup(String world, String user) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroup(user);
    }

    @Override
    public boolean canUserBuild(String world, String user) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().canUserBuild(user);
    }

    @Deprecated
    public String getGroup(String name) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroup(name);
    }

    @Override
    public String[] getGroups(String world, String name) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroups(name);
    }

    @Override
    public boolean inGroup(String world, String name, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().inGroup(name, group);
    }

    @Deprecated
    public boolean inGroup(String name, String group) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().inGroup(name, group);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupPrefix(group);
    }

    @Deprecated
    public String getGroupPrefix(String group) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupPrefix(group);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupSuffix(group);
    }

    @Deprecated
    public String getGroupSuffix(String group) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupSuffix(group);
    }

    @Override
    public boolean canGroupBuild(String world, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().canGroupBuild(group);
    }

    @Deprecated
    public boolean canGroupBuild(String group) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().canGroupBuild(group);
    }

    @Override
    public String getGroupPermissionString(String world, String group, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupPermissionString(group, permission);
    }

    @Deprecated
    public String getGroupPermissionString(String group, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupPermissionString(group, permission);
    }

    @Override
    public int getGroupPermissionInteger(String world, String group, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupPermissionInteger(group, permission);
    }

    @Deprecated
    public int getGroupPermissionInteger(String group, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupPermissionInteger(group, permission);
    }

    @Override
    public boolean getGroupPermissionBoolean(String world, String group, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupPermissionBoolean(group, permission);
    }

    @Deprecated
    public boolean getGroupPermissionBoolean(String group, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupPermissionBoolean(group, permission);
    }

    @Override
    public double getGroupPermissionDouble(String world, String group, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupPermissionDouble(group, permission);
    }

    @Deprecated
    public double getGroupPermissionDouble(String group, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupPermissionDouble(group, permission);
    }

    @Override
    public String getUserPermissionString(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getUserPermissionString(name, permission);
    }

    @Deprecated
    public String getUserPermissionString(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getUserPermissionString(name, permission);
    }

    @Override
    public int getUserPermissionInteger(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getUserPermissionInteger(name, permission);
    }

    @Deprecated
    public int getUserPermissionInteger(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getUserPermissionInteger(name, permission);
    }

    @Override
    public boolean getUserPermissionBoolean(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getUserPermissionBoolean(name, permission);
    }

    @Deprecated
    public boolean getUserPermissionBoolean(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getUserPermissionBoolean(name, permission);
    }

    @Override
    public double getUserPermissionDouble(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getUserPermissionDouble(name, permission);
    }

    @Deprecated
    public double getUserPermissionDouble(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getUserPermissionDouble(name, permission);
    }

    @Override
    public String getPermissionString(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getPermissionString(name, permission);
    }

    @Deprecated
    public String getPermissionString(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getPermissionString(name, permission);
    }

    @Override
    public int getPermissionInteger(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getPermissionInteger(name, permission);
    }

    @Deprecated
    public int getPermissionInteger(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getPermissionInteger(name, permission);
    }

    @Override
    public boolean getPermissionBoolean(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getPermissionBoolean(name, permission);
    }

    @Deprecated
    public boolean getPermissionBoolean(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getPermissionBoolean(name, permission);
    }

    @Override
    public double getPermissionDouble(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getPermissionDouble(name, permission);
    }

    @Deprecated
    public double getPermissionDouble(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getPermissionDouble(name, permission);
    }

    public void setGM(Plugin p) {
        this.plugin = (GroupManager) p;
    }

    @Override
    public boolean canGroupRawBuild(String world, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().canGroupBuild(group);
    }

    @Override
    public void closeAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareWeights(String firstWorld, String first, String secondWorld, String second) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareWeights(String world, String first, String second) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Set<String>> getAllGroups(String world, String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Group getDefaultGroup(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Group getGroupObject(String world, String group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getGroupProperName(String world, String group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean getInfoBoolean(String world, String entryName, String path,
            boolean isGroup) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getInfoBoolean(entryName, path, isGroup);
    }

    @Override
    public Boolean getInfoBoolean(String world, String entryName, String path,
            boolean isGroup, Comparator<Boolean> comparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Double getInfoDouble(String world, String entryName, String path, boolean isGroup) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getInfoDouble(entryName, path, isGroup);
    }

    @Override
    public Double getInfoDouble(String world, String entryName, String path,
            boolean isGroup, Comparator<Double> comparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getInfoInteger(String world, String entryName, String path,
            boolean isGroup) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getInfoInteger(entryName, path, isGroup);
    }

    @Override
    public Integer getInfoInteger(String world, String entryName, String path,
            boolean isGroup, Comparator<Integer> comparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getInfoString(String world, String entryName, String path, boolean isGroup) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getInfoString(entryName, path, isGroup);
    }

    @Override
    public String getInfoString(String world, String entryName, String path,
            boolean isGroup, Comparator<String> comparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean getRawInfoBoolean(String world, String entryName, String path,
            boolean isGroup) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Double getRawInfoDouble(String world, String entryName, String path,
            boolean isGroup) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getRawInfoInteger(String world, String entryName, String path,
            boolean isGroup) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRawInfoString(String world, String entryName, String path,
            boolean isGroup) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getTracks(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public User getUserObject(String world, String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<User> getUsers(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getWorlds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean has(String world, String playerName, String permission) {
        if (permission == null || permission.equals("")) {
            return false;
        }
        if (playerName == null || playerName == "") {
            GroupManager.logger.severe("A plugin is asking permission '" + permission + "' for a null player... Which plugin does that? Bastards!");
            return false;
        }
        if (world == null) {
            GroupManager.logger.warning("The player " + playerName + " has a null world? Treating as default world!");
            return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().permission(playerName, permission);
        }
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().permission(playerName, permission);
    }

    @Override
    public boolean inGroup(String world, String user, String groupWorld, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().inGroup(user, group);
    }

    @Override
    public boolean inSingleGroup(String world, String user, String group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean inSingleGroup(String world, String user, String groupWorld, String group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean permission(String world, String name, String node) {
        return has(world, name, node);
    }

    @Override
    public Group safeGetGroup(String world, String name) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public User safeGetUser(String world, String name) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Group> getGroups(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Group getPrimaryGroupObject(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
