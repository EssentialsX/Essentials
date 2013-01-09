package org.anjocaido.groupmanager.events;

import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.bukkit.Server;

/**
 * @author ElgarL
 * 
 *         Handles all Event generation.
 * 
 */
public class GroupManagerEventHandler {
	
	protected Server server;
	
	public GroupManagerEventHandler(Server server) {
		this.server = server;
	}

	protected void callEvent(GMGroupEvent event) {

		event.schedule(event);
	}

	protected void callEvent(GMUserEvent event) {

		event.schedule(event);
	}

	protected void callEvent(GMSystemEvent event) {

		event.schedule(event);
	}

	public void callEvent(Group group, GMGroupEvent.Action action) {

		callEvent(new GMGroupEvent(group, action));
	}

	public void callEvent(String groupName, GMGroupEvent.Action action) {

		callEvent(new GMGroupEvent(groupName, action));
	}

	public void callEvent(User user, GMUserEvent.Action action) {

		callEvent(new GMUserEvent(user, action));
	}

	public void callEvent(String userName, GMUserEvent.Action action) {

		callEvent(new GMUserEvent(userName, action));
	}

	public void callEvent(GMSystemEvent.Action action) {

		callEvent(new GMSystemEvent(action));
	}
	
	/**
	 * @return the server
	 */
	public Server getServer() {
	
		return server;
	}

	
	/**
	 * @param server the server to set
	 */
	public void setServer(Server server) {
	
		this.server = server;
	}
}