package org.anjocaido.groupmanager.events;


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
	
	
}