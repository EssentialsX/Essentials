/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.dataholder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.anjocaido.groupmanager.data.User;

/**
 * 
 * @author gabrielcouto
 */
public class OverloadedWorldHolder extends WorldDataHolder {

	/**
     *
     */
	protected Map<String, User> overloadedUsers = new HashMap<String, User>();

	/**
	 * 
	 * @param ph
	 */
	public OverloadedWorldHolder(WorldDataHolder ph) {

		super(ph.getName());
		this.setGroupsFile(ph.getGroupsFile());
		this.setUsersFile(ph.getUsersFile());
		this.groups = ph.groups;
		this.users = ph.users;
	}

	/**
	 * 
	 * @param userName
	 * @return user object or a new user if none exists.
	 */
	@Override
	public User getUser(String userName) {

		//OVERLOADED CODE
		String userNameLowered = userName.toLowerCase();
		if (overloadedUsers.containsKey(userNameLowered)) {
			return overloadedUsers.get(userNameLowered);
		}
		//END CODE
		if (getUsers().containsKey(userNameLowered)) {
			return getUsers().get(userNameLowered);
		}
		User newUser = createUser(userName);
		setUsersChanged(true);
		return newUser;
	}

	/**
	 * 
	 * @param theUser
	 */
	@Override
	public void addUser(User theUser) {

		if (theUser.getDataSource() != this) {
			theUser = theUser.clone(this);
		}
		if (theUser == null) {
			return;
		}
		if ((theUser.getGroup() == null) || (!getGroups().containsKey(theUser.getGroupName().toLowerCase()))) {
			theUser.setGroup(getDefaultGroup());
		}
		//OVERLOADED CODE
		if (overloadedUsers.containsKey(theUser.getName().toLowerCase())) {
			overloadedUsers.remove(theUser.getName().toLowerCase());
			overloadedUsers.put(theUser.getName().toLowerCase(), theUser);
			return;
		}
		//END CODE
		removeUser(theUser.getName());
		getUsers().put(theUser.getName().toLowerCase(), theUser);
		setUsersChanged(true);
	}

	/**
	 * 
	 * @param userName
	 * @return true if removed/false if not found.
	 */
	@Override
	public boolean removeUser(String userName) {

		//OVERLOADED CODE
		if (overloadedUsers.containsKey(userName.toLowerCase())) {
			overloadedUsers.remove(userName.toLowerCase());
			return true;
		}
		//END CODE
		if (getUsers().containsKey(userName.toLowerCase())) {
			getUsers().remove(userName.toLowerCase());
			setUsersChanged(true);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeGroup(String groupName) {

		if (groupName.equals(getDefaultGroup())) {
			return false;
		}
		for (String key : getGroups().keySet()) {
			if (groupName.equalsIgnoreCase(key)) {
				getGroups().remove(key);
				for (String userKey : getUsers().keySet()) {
					User user = getUsers().get(userKey);
					if (user.getGroupName().equalsIgnoreCase(key)) {
						user.setGroup(getDefaultGroup());
					}

				}
				//OVERLOADED CODE
				for (String userKey : overloadedUsers.keySet()) {
					User user = overloadedUsers.get(userKey);
					if (user.getGroupName().equalsIgnoreCase(key)) {
						user.setGroup(getDefaultGroup());
					}

				}
				//END OVERLOAD
				setGroupsChanged(true);
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return Collection of all users
	 */
	@Override
	public Collection<User> getUserList() {

		Collection<User> overloadedList = new ArrayList<User>();
		Collection<User> normalList = getUsers().values();
		for (User u : normalList) {
			if (overloadedUsers.containsKey(u.getName().toLowerCase())) {
				overloadedList.add(overloadedUsers.get(u.getName().toLowerCase()));
			} else {
				overloadedList.add(u);
			}
		}
		return overloadedList;
	}

	/**
	 * 
	 * @param userName
	 * @return true if user is overloaded.
	 */
	public boolean isOverloaded(String userName) {

		return overloadedUsers.containsKey(userName.toLowerCase());
	}

	/**
	 * 
	 * @param userName
	 */
	public void overloadUser(String userName) {

		if (!isOverloaded(userName)) {
			User theUser = getUser(userName);
			theUser = theUser.clone();
			if (overloadedUsers.containsKey(theUser.getName().toLowerCase())) {
				overloadedUsers.remove(theUser.getName().toLowerCase());
			}
			overloadedUsers.put(theUser.getName().toLowerCase(), theUser);
		}
	}

	/**
	 * 
	 * @param userName
	 */
	public void removeOverload(String userName) {

		overloadedUsers.remove(userName.toLowerCase());
	}

	/**
	 * Gets the user in normal state. Surpassing the overload state.
	 * It doesn't affect permissions. But it enables plugins change the
	 * actual user permissions even in overload mode.
	 * 
	 * @param userName
	 * @return user object
	 */
	public User surpassOverload(String userName) {

		if (!isOverloaded(userName)) {
			return getUser(userName);
		}
		if (getUsers().containsKey(userName.toLowerCase())) {
			return getUsers().get(userName.toLowerCase());
		}
		User newUser = createUser(userName);
		return newUser;
	}
}