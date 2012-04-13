package org.anjocaido.groupmanager.events;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author ElgarL
 * 
 */
public class GMGroupEvent extends Event {

	/**
	 * 
	 */
	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {

		return handlers;
	}

	public static HandlerList getHandlerList() {

		return handlers;
	}

	//////////////////////////////

	protected Group group;

	protected String groupName;

	protected Action action;

	public GMGroupEvent(Group group, Action action) {

		super();

		this.group = group;
		this.action = action;
		this.groupName = group.getName();
	}

	public GMGroupEvent(String groupName, Action action) {

		super();

		this.groupName = groupName;
		this.action = action;
	}

	public Action getAction() {

		return this.action;
	}

	public Group getGroup() {

		return group;
	}

	public String getGroupName() {

		return groupName;
	}

	public enum Action {
		GROUP_PERMISSIONS_CHANGED, GROUP_INHERITANCE_CHANGED, GROUP_INFO_CHANGED, GROUP_ADDED, GROUP_REMOVED,
	}

	public void schedule(final GMGroupEvent event) {

		if (Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("GroupManager"), new Runnable() {

			@Override
			public void run() {

				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		}, 1) == -1)
			GroupManager.logger.warning("Could not schedule GM Event.");
	}
}