package org.anjocaido.groupmanager.events;

import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;

/**
 * @author ElgarL
 * 
 *         Handles all Event generation.
 * 
 */
public class GroupManagerEventHandler {

	protected static void callEvent(GMGroupEvent event) {

		event.schedule(event);
	}

	protected static void callEvent(GMUserEvent event) {

		event.schedule(event);
	}

	protected static void callEvent(GMSystemEvent event) {

		event.schedule(event);
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