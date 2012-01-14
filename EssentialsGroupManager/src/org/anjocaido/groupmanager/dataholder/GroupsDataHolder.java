package org.anjocaido.groupmanager.dataholder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.anjocaido.groupmanager.data.Group;



/**
 * @author ElgarL
 * 
 */
public class GroupsDataHolder {

	/**
	 * Root World name this set of groups is associated with.
	 */
	private String name;
	private Group defaultGroup = null;
	private File groupsFile;
	private boolean haveGroupsChanged = false;
	private long timeStampGroups = 0;

	/**
	 * The actual groups holder
	 */
	private Map<String, Group> groups = new HashMap<String, Group>();

	/**
	 * Constructor
	 */
	protected GroupsDataHolder() {
	}

	protected void setWorldName(String worldName) {
		name = worldName;
	}

	/**
	 * @return the name
	 */
	public String getWorldName() {
		return name;
	}

	/**
	 * @return the defaultGroup
	 */
	public Group getDefaultGroup() {
		return defaultGroup;
	}

	/**
	 * @param defaultGroup the defaultGroup to set
	 */
	public void setDefaultGroup(Group defaultGroup) {
		this.defaultGroup = defaultGroup;
	}

	/**
	 * @return the groups
	 */
	public Map<String, Group> getGroups() {
		return groups;
	}
	
	/**
	 * @param groups the groups to set
	 */
	public void setGroups(Map<String, Group> groups) {
		this.groups = groups;
	}
	
	/**
	 * @return the groupsFile
	 */
	public File getGroupsFile() {
		return groupsFile;
	}

	/**
	 * @param groupsFile the groupsFile to set
	 */
	public void setGroupsFile(File groupsFile) {
		this.groupsFile = groupsFile;
	}

	/**
	 * @return the haveGroupsChanged
	 */
	public boolean HaveGroupsChanged() {
		return haveGroupsChanged;
	}

	/**
	 * @param haveGroupsChanged the haveGroupsChanged to set
	 */
	public void setGroupsChanged(boolean haveGroupsChanged) {
		this.haveGroupsChanged = haveGroupsChanged;
	}

	/**
	 * @return the timeStampGroups
	 */
	public long getTimeStampGroups() {
		return timeStampGroups;
	}

	/**
	 * @param timeStampGroups the timeStampGroups to set
	 */
	public void setTimeStampGroups(long timeStampGroups) {
		this.timeStampGroups = timeStampGroups;
	}

}