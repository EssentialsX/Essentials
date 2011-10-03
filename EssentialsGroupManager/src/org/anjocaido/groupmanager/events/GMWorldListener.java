package org.anjocaido.groupmanager.events;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.event.Event;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldListener;


/**
 * @author ElgarL
 * 
 * Handle new world creation from other plugins
 *
 */
public class GMWorldListener extends WorldListener {
	
	private final GroupManager plugin;

	public GMWorldListener(GroupManager instance) {
		plugin = instance;
		registerEvents();
	}
	
	private void registerEvents() {
    	plugin.getServer().getPluginManager().registerEvent(Event.Type.WORLD_INIT, this, Event.Priority.Lowest, plugin);
    }
	
	@Override
	public void onWorldInit(WorldInitEvent event) {
		String worldName =  event.getWorld().getName();
		
		if (GroupManager.isLoaded() && !plugin.getWorldsHolder().isInList(worldName)) {	
			GroupManager.logger.info("New world detected...");
			GroupManager.logger.info("Creating data for: " + worldName);
			plugin.getWorldsHolder().setupWorldFolder(worldName);
			plugin.getWorldsHolder().loadWorld(worldName);
			if (plugin.getWorldsHolder().isInList(worldName)) {
				GroupManager.logger.info("Don't forget to configure/mirror this world in config.yml.");
			} else
				GroupManager.logger.severe("Failed to configure this world.");
		}
	}
}