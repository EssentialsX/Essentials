package org.anjocaido.groupmanager.events;

import org.anjocaido.groupmanager.data.Group;


/**
 * @author ElgarL
 *
 */
public class GMGroupEvent extends GroupManagerEvent {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5294917600434510451L;

	protected Group group;
	
	protected String groupName;
    
    protected Action action;

    public GMGroupEvent(Group group, Action action) {
        super(action.toString());

        this.group = group;
        this.action = action;
    }
    
    public GMGroupEvent(String groupName, Action action) {
        super(action.toString());

        this.groupName = groupName;
        this.action = action;
    }
    
    public Action getAction(){
        return this.action;
    }

    public Group getGroup() {
        return group;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public enum Action {
        GROUP_PERMISSIONS_CHANGED,
        GROUP_INHERITANCE_CHANGED,
        GROUP_INFO_CHANGED,
        GROUP_ADDED,
        GROUP_REMOVED,
    }
}