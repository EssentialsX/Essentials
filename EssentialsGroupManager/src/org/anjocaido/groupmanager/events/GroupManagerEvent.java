package org.anjocaido.groupmanager.events;


import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

/**
 * @author ElgarL
 *
 */
public abstract class GroupManagerEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8790362185329926951L;
	
	protected GroupManagerEvent(String name) {
		super(name);
	}
	
	/**
	 * Triggers all GroupManager events for other plugins to see.
	 * Schedules events for 1 tick later to allow GM to finish populating super perms.
	 * 
	 * @param event
	 */
	public void schedule(final GroupManagerEvent event) {

			if (Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("GroupManager"), new Runnable() {

				@Override
				public void run() {
					Bukkit.getServer().getPluginManager().callEvent(event);
				}
			}, 1) == -1)
				GroupManager.logger.warning("Could not schedule GM Event.");
	}
	
	
}