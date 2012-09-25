package org.anjocaido.groupmanager.dataholder;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.anjocaido.groupmanager.data.User;

/**
 * This container holds all Users loaded from the relevant usersFile.
 * 
 * @author ElgarL
 * 
 */
public class UsersDataHolder {

	private WorldDataHolder dataSource;
	private File usersFile;
	private boolean haveUsersChanged = false;
	private long timeStampUsers = 0;

	/**
	 * The actual groups holder
	 */
	private final Map<String, User> users = Collections.synchronizedMap(new HashMap<String, User>());

	/**
	 * Constructor
	 */
	protected UsersDataHolder() {

	}

	public void setDataSource(WorldDataHolder dataSource) {

		this.dataSource = dataSource;
		//push this data source to the users, so they pull the correct groups data.
		synchronized(users) {
		for (User user : users.values())
			user.setDataSource(this.dataSource);
		}
	}

	/**
	 * Note: Iteration over this object has to be synchronized!
	 * @return the users
	 */
	public Map<String, User> getUsers() {

		return users;
	}

	/**
	 * Resets the Users
	 */
	public void resetUsers() {
		this.users.clear();
	}

	/**
	 * @return the usersFile
	 */
	public File getUsersFile() {

		return usersFile;
	}

	/**
	 * @param usersFile the usersFile to set
	 */
	public void setUsersFile(File usersFile) {

		this.usersFile = usersFile;
	}

	/**
	 * @return the haveUsersChanged
	 */
	public boolean HaveUsersChanged() {

		return haveUsersChanged;
	}

	/**
	 * @param haveUsersChanged the haveUsersChanged to set
	 */
	public void setUsersChanged(boolean haveUsersChanged) {

		this.haveUsersChanged = haveUsersChanged;
	}

	/**
	 * @return the timeStampUsers
	 */
	public long getTimeStampUsers() {

		return timeStampUsers;
	}

	/**
	 * @param timeStampUsers the timeStampUsers to set
	 */
	public void setTimeStampUsers(long timeStampUsers) {

		this.timeStampUsers = timeStampUsers;
	}

}