package org.anjocaido.groupmanager.events;


/**
 * @author ElgarL
 *
 */
public class GMSystemEvent extends GroupManagerEvent {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8786811924448821548L;
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