package org.anjocaido.groupmanager.events;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;



/**
 * @author ElgarL
 *
 */
public class GroupManagerEventHandler {
	
	protected static void callEvent(GMGroupEvent event) {
		GroupManager.callEvent(event);
	}
	protected static void callEvent(GMUserEvent event) {
		GroupManager.callEvent(event);
	}
	protected static void callEvent(GMSystemEvent event) {
		GroupManager.callEvent(event);
	}

	public static void callEvent(Group group, GMGroupEvent.Action action) {
		callEvent(new GMGroupEvent(group, action));
	}
	public static void callEvent(String groupName, GMGroupEvent.Action action) {
		callEvent(new GMGroupEvent(groupName, action));
	}
	
	public static void callEvent(User user, GMUserEvent.Action action) {
		callEvent(new GMUserEvent(user, action));
	}
	public static void callEvent(String userName, GMUserEvent.Action action) {
		callEvent(new GMUserEvent(userName, action));
	}
	
	public static void callEvent(GMSystemEvent.Action action) {
		callEvent(new GMSystemEvent(action));
	}
}