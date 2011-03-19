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
        this.f = ph.f;
        this.groupsFile = ph.groupsFile;
        this.usersFile = ph.usersFile;
        this.defaultGroup = ph.defaultGroup;
        this.groups = ph.groups;
        this.users = ph.users;
    }

    /**
     *
     * @param userName
     * @return
     */
    @Override
    public User getUser(String userName) {
        //OVERLOADED CODE
        if (overloadedUsers.containsKey(userName.toLowerCase())) {
            return overloadedUsers.get(userName.toLowerCase());
        }
        //END CODE
        if (users.containsKey(userName.toLowerCase())) {
            return users.get(userName.toLowerCase());
        }
        User newUser = createUser(userName);
        haveUsersChanged = true;
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
        if ((theUser.getGroup() == null) || (!groups.containsKey(theUser.getGroupName().toLowerCase()))) {
            theUser.setGroup(defaultGroup);
        }
        //OVERLOADED CODE
        if (overloadedUsers.containsKey(theUser.getName().toLowerCase())) {
            overloadedUsers.remove(theUser.getName().toLowerCase());
            overloadedUsers.put(theUser.getName().toLowerCase(), theUser);
            return;
        }
        //END CODE
        removeUser(theUser.getName());
        users.put(theUser.getName().toLowerCase(), theUser);
        haveUsersChanged = true;
    }

    /**
     *
     * @param userName
     * @return
     */
    @Override
    public boolean removeUser(String userName) {
        //OVERLOADED CODE
        if (overloadedUsers.containsKey(userName.toLowerCase())) {
            overloadedUsers.remove(userName.toLowerCase());
            return true;
        }
        //END CODE
        if (users.containsKey(userName.toLowerCase())) {
            users.remove(userName.toLowerCase());
            haveUsersChanged = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean removeGroup(String groupName) {
        if (groupName.equals(defaultGroup)) {
            return false;
        }
        for (String key : groups.keySet()) {
            if (groupName.equalsIgnoreCase(key)) {
                groups.remove(key);
                for (String userKey : users.keySet()) {
                    User user = users.get(userKey);
                    if (user.getGroupName().equalsIgnoreCase(key)) {
                        user.setGroup(defaultGroup);
                    }

                }
                //OVERLOADED CODE
                for (String userKey : overloadedUsers.keySet()) {
                    User user = overloadedUsers.get(userKey);
                    if (user.getGroupName().equalsIgnoreCase(key)) {
                        user.setGroup(defaultGroup);
                    }

                }
                //END OVERLOAD
                haveGroupsChanged = true;
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return
     */
    @Override
    public Collection<User> getUserList() {
        Collection<User> overloadedList = new ArrayList<User>();
        Collection<User> normalList = users.values();
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
     * @return
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
     *  Gets the user in normal state. Surpassing the overload state.
     * It doesn't affect permissions. But it enables plugins change the
     * actual user permissions even in overload mode.
     * @param userName
     * @return
     */
    public User surpassOverload(String userName) {
        if (!isOverloaded(userName)) {
            return getUser(userName);
        }
        if (users.containsKey(userName.toLowerCase())) {
            return users.get(userName.toLowerCase());
        }
        User newUser = createUser(userName);
        return newUser;
    }
}
