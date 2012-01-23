package org.anjocaido.groupmanager.events;

import org.bukkit.event.HandlerList;


/**
 * @author ElgarL
 *
 */
public class GMSystemEvent extends GroupManagerEvent {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8786811924448821548L;
	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

    //////////////////////////////
    
	protected Action action;
    
    public GMSystemEvent(Action action) {
        super(action.toString());
        
        this.action = action;
    }
    
    public Action getAction(){
        return this.action;
    }
    
    public enum Action {
        RELOADED,
        SAVED,
        DEFAULT_GROUP_CHANGED,
        VALIDATE_TOGGLE,
    }
}