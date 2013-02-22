package org.anjocaido.groupmanager.events;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

/**
 * @author ElgarL
 * 
 *         Handle new world creation from other plugins
 * 
 */
public class GMWorldListener implements Listener {

	private final GroupManager plugin;

	public GMWorldListener(GroupManager instance) {

		plugin = instance;
		registerEvents();
	}

	private void registerEvents() {

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldInit(WorldInitEvent event) {

		String worldName = event.getWorld().getName();

		if (GroupManager.isLoaded() && !plugin.getWorldsHolder().isInList(worldName)) {
			GroupManager.logger.info("New world detected...");
			GroupManager.logger.info("Creating data for: " + worldName);
			
			if (plugin.getWorldsHolder().getWorldData("all_unnamed_worlds") != null) {
				
				String usersMirror = plugin.getWorldsHolder().getMirrorsUser().get("all_unnamed_worlds");
				String groupsMirror = plugin.getWorldsHolder().getMirrorsGroup().get("all_unnamed_worlds");
				
				if (usersMirror != null)
					plugin.getWorldsHolder().getMirrorsUser().put(worldName.toLowerCase(), usersMirror);
				
				if (groupsMirror != null)
					plugin.getWorldsHolder().getMirrorsGroup().put(worldName.toLowerCase(), groupsMirror);
				
			}
			
			plugin.getWorldsHolder().setupWorldFolder(worldName);
			plugin.getWorldsHolder().loadWorld(worldName);
			
			
			if (plugin.getWorldsHolder().isInList(worldName)) {
				GroupManager.logger.info("Don't forget to configure/mirror this world in config.yml.");
			} else
				GroupManager.logger.severe("Failed to configure this world.");
		}
	}
}