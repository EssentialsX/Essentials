/*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
*/

package org.anjocaido.groupmanager.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.utils.PermissionCheckResult;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
//import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


/**
 *
 * BukkitPermissions overrides to force GM reponses to Superperms
 *
 * @author ElgarL, based upon PermissionsEX implementation
 */
public class BukkitPermissions {

    protected Map<Player, PermissionAttachment> attachments = new HashMap<Player, PermissionAttachment>();
    protected Set<Permission> registeredPermissions = new HashSet<Permission>();
    protected GroupManager plugin;
    protected boolean dumpAllPermissions = true;
    protected boolean dumpMatchedPermissions = true;
    public boolean player_join = false;

    public BukkitPermissions(GroupManager plugin) {
        this.plugin = plugin;

        this.collectPermissions();
        this.registerEvents();

        this.updateAllPlayers();

        GroupManager.logger.info("Superperms support enabled.");
    }

    private void registerEvents() {
        PluginManager manager = plugin.getServer().getPluginManager();

        PlayerEvents playerEventListener = new PlayerEvents();

        manager.registerEvent(Event.Type.PLAYER_JOIN, playerEventListener, Event.Priority.Normal, plugin);
        manager.registerEvent(Event.Type.PLAYER_KICK, playerEventListener, Event.Priority.Normal, plugin);
        manager.registerEvent(Event.Type.PLAYER_QUIT, playerEventListener, Event.Priority.Normal, plugin);

        manager.registerEvent(Event.Type.PLAYER_RESPAWN, playerEventListener, Event.Priority.Normal, plugin);
        manager.registerEvent(Event.Type.PLAYER_TELEPORT, playerEventListener, Event.Priority.Normal, plugin);
        manager.registerEvent(Event.Type.PLAYER_PORTAL, playerEventListener, Event.Priority.Normal, plugin);

        ServerListener serverListener = new BukkitEvents();

        manager.registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Event.Priority.Normal, plugin);
        manager.registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Event.Priority.Normal, plugin);
    }

    private void collectPermissions() {
        registeredPermissions.clear();
        for (Plugin bukkitPlugin : Bukkit.getServer().getPluginManager().getPlugins()) {
        	for(Permission permission : bukkitPlugin.getDescription().getPermissions())
            registeredPermissions.add(permission);
        }
    }
    
    public void updatePermissions(Player player){
        this.updatePermissions(player, null);
    }
    
    public void updatePermissions(Player player, String world) {
        if (player == null || !GroupManager.isLoaded) {
            return;
        }

        if (!this.attachments.containsKey(player)) {
            this.attachments.put(player, player.addAttachment(plugin));
        }

        if(world == null){
            world = player.getWorld().getName();
        }
        
        // All permissions registered with Bukkit for this player
        PermissionAttachment attachment = this.attachments.get(player);
        
        OverloadedWorldHolder worldData = plugin.getWorldsHolder().getWorldData(world);

        User user = worldData.getUser(player.getName());
        
        // clear permissions
        for (String permission : attachment.getPermissions().keySet()) {
            attachment.unsetPermission(permission);
        }      
        
        // find matching permissions
        PermissionCheckResult permissionResult;
        Boolean value;
        for (Permission permission : registeredPermissions) {
        	permissionResult = worldData.getPermissionsHandler().checkFullUserPermission(user, permission.getName());
            if (permissionResult.resultType.equals(PermissionCheckResult.Type.FOUND))
            	value = true;
            else
            	value = false;
        	
            attachment.setPermission(permission, value);
        }
        
        // Add any missing permissions for this player (non bukkit plugins)
        List<String> playerPermArray = new ArrayList<String>(worldData.getPermissionsHandler().getAllPlayersPermissions(player.getName()));
        
        for (String permission : playerPermArray) {
        	value = true;
            if (permission.startsWith("-")) {
                permission = permission.substring(1); // cut off -
                value = false;
            }

            if (!attachment.getPermissions().containsKey(permission)) {
                attachment.setPermission(permission, value);
            }
        }
        player.recalculatePermissions();
        
        /*
        // List perms for this player
        GroupManager.logger.info("Attachment Permissions:");
        for(Map.Entry<String, Boolean> entry : attachment.getPermissions().entrySet()){
        	GroupManager.logger.info(" " + entry.getKey() + " = " + entry.getValue());
        }

        GroupManager.logger.info("Effective Permissions:");
        for(PermissionAttachmentInfo info : player.getEffectivePermissions()){
        	GroupManager.logger.info(" " + info.getPermission() + " = " + info.getValue());
        }
		*/
    }
    
    public List<String> listPerms(Player player) {
    	List<String> perms = new ArrayList<String>();
    	
    	/*
    	// All permissions registered with Bukkit for this player
        PermissionAttachment attachment = this.attachments.get(player);
    	
    	// List perms for this player
    	perms.add("Attachment Permissions:");
        for(Map.Entry<String, Boolean> entry : attachment.getPermissions().entrySet()){
        	perms.add(" " + entry.getKey() + " = " + entry.getValue());
        }
        */

        perms.add("Effective Permissions:");
        for(PermissionAttachmentInfo info : player.getEffectivePermissions()){
        	if (info.getValue() == true)
        		perms.add(" " + info.getPermission() + " = " + info.getValue());
        }
        return perms;
    }

    public void updateAllPlayers() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            updatePermissions(player);
        }
    }

    protected class PlayerEvents extends PlayerListener {

        @Override
        public void onPlayerJoin(PlayerJoinEvent event) {
        	player_join = true;
        	Player player = event.getPlayer();
        	//force GM to create the player if they are not already listed.
        	if (plugin.getWorldsHolder().getWorldData(player.getWorld().getName()).getUser(player.getName()) != null) {
        		player_join = false;
        		updatePermissions(event.getPlayer());
        	} else
        		player_join = false;
        }

        @Override
        public void onPlayerPortal(PlayerPortalEvent event) { // will portal into another world
            if(!event.getFrom().getWorld().equals(event.getTo().getWorld())){ // only if world actually changed
            	updatePermissions(event.getPlayer(), event.getTo().getWorld().getName());
            }
        }

        @Override
        public void onPlayerRespawn(PlayerRespawnEvent event) { // can be respawned in another world
            updatePermissions(event.getPlayer(), event.getRespawnLocation().getWorld().getName());
        }

        @Override
        public void onPlayerTeleport(PlayerTeleportEvent event) { // can be teleported into another world
            if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) { // only if world actually changed
                updatePermissions(event.getPlayer(), event.getTo().getWorld().getName());
            }
        }

        @Override
        public void onPlayerQuit(PlayerQuitEvent event) {
            attachments.remove(event.getPlayer());
        }

        @Override
        public void onPlayerKick(PlayerKickEvent event) {
            attachments.remove(event.getPlayer());
        }
    }

    protected class BukkitEvents extends ServerListener {

        @Override
        public void onPluginEnable(PluginEnableEvent event) {
            collectPermissions();
            updateAllPlayers();
        }

        @Override
        public void onPluginDisable(PluginDisableEvent event) {
            //collectPermissions();
            //updateAllPlayers();
        }
    }

}