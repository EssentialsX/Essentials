package org.anjocaido.groupmanager.events;

import org.anjocaido.groupmanager.data.User;
import org.bukkit.event.HandlerList;


/**
 * @author ElgarL
 *
 */
public class GMUserEvent extends GroupManagerEvent {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5294917600434510451L;
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
        super(action.toString());

        this.user = user;
        this.action = action;
        this.userName = user.getName();
    }
    
    public GMUserEvent(String userName, Action action) {
        super(action.toString());

        this.userName = userName;
        this.action = action;
    }
    
    public Action getAction(){
        return this.action;
    }

    public User getUser() {
        return user;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public enum Action {
        USER_PERMISSIONS_CHANGED,
        USER_INHERITANCE_CHANGED,
        USER_INFO_CHANGED,
        USER_GROUP_CHANGED,
        USER_SUBGROUP_CHANGED,
        USER_ADDED,
        USER_REMOVED,
    }
}