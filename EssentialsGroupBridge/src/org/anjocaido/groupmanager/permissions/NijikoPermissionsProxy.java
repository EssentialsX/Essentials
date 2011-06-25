/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.permissions;

import com.nijiko.permissions.Control;
import java.io.File;
import java.util.Map;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

/**
 *  Everything here maintains the model created by Nijikokun
 * 
 * But implemented to use GroupManager system. Which provides instant changes,
 * without file access.
 *
 * @author gabrielcouto
 */
public class NijikoPermissionsProxy extends Control {
    GroupManager plugin;
    public NijikoPermissionsProxy(GroupManager plugin){
        super(null);
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeGroupInfo(String world, String group, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeUserInfo(String world, String user, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeUserInfo(String user, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addGroupPermission(String group, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeGroupPermission(String group, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addGroupInfo(String group, String node, Object data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeGroupInfo(String group, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addUserPermission(String user, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeUserPermission(String user, String node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addUserInfo(String user, String node, Object data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDefaultWorld(String world) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDirectory(File directory) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean loadWorld(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void forceLoadWorld(String world) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean checkWorld(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void load() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void load(String world, Configuration config) {
        //throw new UnsupportedOperationException("Not supported yet.");
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
    public void setCache(Map<String, Boolean> Cache) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCache(String world, Map<String, Boolean> Cache) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCacheItem(String player, String permission, boolean data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCacheItem(String world, String player, String permission, boolean data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Boolean> getCache() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Boolean> getCache(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getCacheItem(String player, String permission) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getCacheItem(String world, String player, String permission) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeCachedItem(String player, String permission) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeCachedItem(String world, String player, String permission) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearCache() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearCache(String world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearAllCache() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean has(Player player, String permission) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return permission(player, permission);
    }

    @Override
    public boolean permission(Player player, String permission) {
        //throw new UnsupportedOperationException("Not supported yet.");
        if(permission==null || permission.equals("")){
            return false;
        }
        if(player==null){
            GroupManager.logger.severe("A plugin is asking permission '"+permission+"' for a null player... Which plugin does that? Bastards!");
            return false;
        }
        if(player.getWorld()==null){
            GroupManager.logger.warning("The player "+player.getName()+" has a null world? Treating as default world!");
            return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().has(player, permission);
        }
        return plugin.getWorldsHolder().getWorldData(player.getWorld().getName()).getPermissionsHandler().has(player, permission);
    }

    @Override
    public String getGroup(String world, String name) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroup(name);
    }

    @Deprecated
    @Override
    public String getGroup(String name) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroup(name);
    }

    @Override
    public String[] getGroups(String world, String name) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroups(name);
    }

    @Deprecated
    @Override
    public String[] getGroups(String name) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroups(name);
    }

    @Override
    public boolean inGroup(String world, String name, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().inGroup(name,group);
    }

    @Deprecated
    @Override
    public boolean inGroup(String name, String group) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().inGroup(name,group);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupPrefix(group);
    }

    @Override
    public String getGroupPrefix(String group) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupPrefix(group);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupSuffix(group);
    }

    @Override
    public String getGroupSuffix(String group) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupSuffix(group);
    }

    @Override
    public boolean canGroupBuild(String world, String group) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().canGroupBuild(group);
    }

    @Deprecated
    @Override
    public boolean canGroupBuild(String group) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().canGroupBuild(group);
    }

    @Override
    public String getGroupPermissionString(String world, String group, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupPermissionString(group,permission);
    }

    @Override
    public String getGroupPermissionString(String group, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupPermissionString(group,permission);
    }

    @Override
    public int getGroupPermissionInteger(String world, String group, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupPermissionInteger(group,permission);
    }

    @Override
    public int getGroupPermissionInteger(String group, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupPermissionInteger(group,permission);
    }

    @Override
    public boolean getGroupPermissionBoolean(String world, String group, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupPermissionBoolean(group,permission);
    }

    @Override
    public boolean getGroupPermissionBoolean(String group, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupPermissionBoolean(group,permission);
    }

    @Override
    public double getGroupPermissionDouble(String world, String group, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getGroupPermissionDouble(group,permission);
    }

    @Override
    public double getGroupPermissionDouble(String group, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getGroupPermissionDouble(group,permission);
    }

    @Override
    public String getUserPermissionString(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getUserPermissionString(name,permission);
    }

    @Override
    public String getUserPermissionString(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getUserPermissionString(name,permission);
    }

    @Override
    public int getUserPermissionInteger(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getUserPermissionInteger(name,permission);
    }

    @Override
    public int getUserPermissionInteger(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getUserPermissionInteger(name,permission);
    }

    @Override
    public boolean getUserPermissionBoolean(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getUserPermissionBoolean(name,permission);
    }

    @Override
    public boolean getUserPermissionBoolean(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getUserPermissionBoolean(name,permission);
    }

    @Override
    public double getUserPermissionDouble(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getUserPermissionDouble(name,permission);
    }

    @Override
    public double getUserPermissionDouble(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getUserPermissionDouble(name,permission);
    }

    @Override
    public String getPermissionString(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getPermissionString(name,permission);
    }

    @Override
    public String getPermissionString(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getPermissionString(name,permission);
    }

    @Override
    public int getPermissionInteger(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getPermissionInteger(name,permission);
    }

    @Override
    public int getPermissionInteger(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getPermissionInteger(name,permission);
    }

    @Override
    public boolean getPermissionBoolean(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getPermissionBoolean(name,permission);
    }

    @Override
    public boolean getPermissionBoolean(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getPermissionBoolean(name,permission);
    }

    @Override
    public double getPermissionDouble(String world, String name, String permission) {
        return plugin.getWorldsHolder().getWorldData(world).getPermissionsHandler().getPermissionDouble(name,permission);
    }

    @Override
    public double getPermissionDouble(String name, String permission) {
        return plugin.getWorldsHolder().getDefaultWorld().getPermissionsHandler().getPermissionDouble(name,permission);
    }

	public void setGM(Plugin p)
	{
		this.plugin = (GroupManager)p;
	}

}
