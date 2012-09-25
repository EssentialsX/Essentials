package org.anjocaido.groupmanager.dataholder;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.anjocaido.groupmanager.data.Group;

/**
 * This container holds all Groups loaded from the relevant groupsFile.
 * 
 * @author ElgarL
 * 
 */
public class GroupsDataHolder {

	private WorldDataHolder dataSource;
	private Group defaultGroup = null;
	private File groupsFile;
	private boolean haveGroupsChanged = false;
	private long timeStampGroups = 0;

	/**
	 * The actual groups holder
	 */
	private final Map<String, Group> groups = Collections.synchronizedMap(new HashMap<String, Group>());

	/**
	 * Constructor
	 */
	protected GroupsDataHolder() {

	}

	public void setDataSource(WorldDataHolder dataSource) {

		this.dataSource = dataSource;
		//push this data source to the users, so they pull the correct groups data.
		synchronized(groups) {
		for (Group group : groups.values())
			group.setDataSource(this.dataSource);
		}
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
	 * Note: Iteration over this object has to be synchronized!
	 * @return the groups
	 */
	public Map<String, Group> getGroups() {

		return groups;
	}

	/**
	 * 
	 */
	public void resetGroups() {
		this.groups.clear();
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