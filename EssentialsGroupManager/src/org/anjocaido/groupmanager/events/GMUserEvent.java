package org.anjocaido.groupmanager.events;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.User;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author ElgarL
 * 
 */
public class GMUserEvent extends Event {

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

	protected User user;

	protected String userName;

	protected Action action;

	public GMUserEvent(User user, Action action) {

		super();

		this.user = user;
		this.action = action;
		this.userName = user.getName();
	}

	public GMUserEvent(String userName, Action action) {

		super();

		this.userName = userName;
		this.action = action;
	}

	public Action getAction() {

		return this.action;
	}

	public User getUser() {

		return user;
	}

	public String getUserName() {

		return userName;
	}

	public enum Action {
		USER_PERMISSIONS_CHANGED, USER_INHERITANCE_CHANGED, USER_INFO_CHANGED, USER_GROUP_CHANGED, USER_SUBGROUP_CHANGED, USER_ADDED, USER_REMOVED,
	}

	public void schedule(final GMUserEvent event) {

		synchronized (GroupManager.getGMEventHandler().getServer()) {
			if (Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("GroupManager"), new Runnable() {
	
				@Override
				public void run() {
	
					Bukkit.getServer().getPluginManager().callEvent(event);
				}
			}, 1) == -1)
				GroupManager.logger.warning("Could not schedule GM Event.");
		}
	}
}