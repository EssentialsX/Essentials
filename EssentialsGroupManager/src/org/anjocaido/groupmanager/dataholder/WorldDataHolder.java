/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.dataholder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;

/**
 *
 * @author gabrielcouto
 */
public class WorldDataHolder {

    /**
     * World name
     */
    protected String name;
    /**
     * The actual groups holder
     */
    protected Map<String, Group> groups = new HashMap<String, Group>();
	/**
     * The actual users holder
     */
    protected Map<String, User> users = new HashMap<String, User>();
    
	/**
     * Points to the default group
     */
    protected Group defaultGroup = null;
    
    /**
     * The file, which this class loads/save data from/to
     * @deprecated
     */
    @Deprecated
    protected File f;
    
    /**
     *
     */
    protected AnjoPermissionsHandler permissionsHandler;
    /**
     *
     */
    protected File usersFile;
    /**
     *
     */
    protected File groupsFile;
    /**
     *
     */
    protected boolean haveUsersChanged = false;
    /**
     *
     */
    protected boolean haveGroupsChanged = false;
    /**
    *
    */
    protected long timeStampGroups = 0;
	/**
    *
    */
    protected long timeStampUsers = 0;

    
	/**
     * Prevent direct instantiation
     * @param worldName
     */
    protected WorldDataHolder(String worldName) {
        name = worldName;
    }

    /**
     * The main constructor for a new WorldDataHolder
     * Please don't set the default group as null
     * @param worldName
     * @param defaultGroup the default group. its good to start with one
     */
    public WorldDataHolder(String worldName, Group defaultGroup) {
        this.name = worldName;
        groups.put(defaultGroup.getName().toLowerCase(), defaultGroup);
        this.defaultGroup = defaultGroup;
    }

    /**
     * Search for a user. If it doesn't exist, create a new one with
     * default group.
     *
     * @param userName the name of the user
     * @return class that manage that user permission
     */
    public User getUser(String userName) {
        if (users.containsKey(userName.toLowerCase())) {
            return users.get(userName.toLowerCase());
        }
        User newUser = createUser(userName);
        return newUser;
    }

    /**
     * Add a user to the list. If it already exists, overwrite the old.
     * @param theUser the user you want to add to the permission list
     */
    public void addUser(User theUser) {
        if (theUser.getDataSource() != this) {
            theUser = theUser.clone(this);
        }
        if (theUser == null) {
            return;
        }
        if ((theUser.getGroup() == null)) {
            theUser.setGroup(defaultGroup);
        }
        removeUser(theUser.getName());
        users.put(theUser.getName().toLowerCase(), theUser);
        haveUsersChanged = true;
    }

    /**
     * Removes the user from the list. (he might become a default user)
     * @param userName the username from the user to remove
     * @return true if it had something to remove
     */
    public boolean removeUser(String userName) {
        if (users.containsKey(userName.toLowerCase())) {
            users.remove(userName.toLowerCase());
            haveUsersChanged = true;
            return true;
        }
        return false;
    }

    /**
     *
     * @param userName
     * @return true if we have data for this player.
     */
    public boolean isUserDeclared(String userName) {
        return users.containsKey(userName.toLowerCase());
    }

    /**
     * Change the default group of the file.
     * @param group the group you want make default.
     */
    public void setDefaultGroup(Group group) {
        if (!groups.containsKey(group.getName().toLowerCase()) || (group.getDataSource() != this)) {
            addGroup(group);
        }
        defaultGroup = this.getGroup(group.getName());
        haveGroupsChanged = true;
    }

    /**
     * Returns the default group of the file
     * @return the default group
     */
    public Group getDefaultGroup() {
        return defaultGroup;
    }

    /**
     * Returns a group of the given name
     * @param groupName the name of the group
     * @return a group if it is found. null if not found.
     */
    public Group getGroup(String groupName) {
    	if (groupName.startsWith("g:"))
    		return GroupManager.getGlobalGroups().getGroup(groupName);
    	else
    		return groups.get(groupName.toLowerCase());
    }

    /**
     * Check if a group exists.
     * Its the same of getGroup, but check if it is null.
     * @param groupName the name of the group
     * @return true if exists. false if not.
     */
    public boolean groupExists(String groupName) {
    	if (groupName.startsWith("g:"))
    		return GroupManager.getGlobalGroups().hasGroup(groupName);
    	else
    		return groups.containsKey(groupName.toLowerCase());
    }

    /**
     * Add a group to the list
     * @param groupToAdd
     */
    public void addGroup(Group groupToAdd) {
    	if (groupToAdd.getName().startsWith("g:")) {
    		GroupManager.getGlobalGroups().addGroup(groupToAdd);
        	return;
        }
    	
        if (groupToAdd.getDataSource() != this) {
            groupToAdd = groupToAdd.clone(this);
        }
        removeGroup(groupToAdd.getName());
        groups.put(groupToAdd.getName().toLowerCase(), groupToAdd);
        haveGroupsChanged = true;
    }

    /**
     * Remove the group to the list
     * @param groupName
     * @return true if had something to remove. false the group was default or non-existant
     */
    public boolean removeGroup(String groupName) {
    	if (groupName.startsWith("g:")) {
        	return GroupManager.getGlobalGroups().removeGroup(groupName);
        }
    	
        if (defaultGroup != null && groupName.equalsIgnoreCase(defaultGroup.getName())) {
            return false;
        }
        if (groups.containsKey(groupName.toLowerCase())) {
            groups.remove(groupName.toLowerCase());
            haveGroupsChanged = true;
            return true;
        }
        return false;

    }

    /**
     * Creates a new User with the given name
     * and adds it to this holder.
     * @param userName the username you want
     * @return null if user already exists. or new User
     */
    public User createUser(String userName) {
        if (this.users.containsKey(userName.toLowerCase())) {
            return null;
        }
        User newUser = new User(this, userName);
        newUser.setGroup(defaultGroup);
        this.addUser(newUser);
        haveUsersChanged = true;
        return newUser;
    }

    /**
     * Creates a new Group with the given name
     * and adds it to this holder
     * @param groupName the groupname you want
     * @return null if group already exists. or new Group
     */
    public Group createGroup(String groupName) {
    	if (groupName.startsWith("g:")) {
        	Group newGroup = new Group(groupName);
        	return GroupManager.getGlobalGroups().newGroup(newGroup);
        }
    	
    	if (this.groups.containsKey(groupName.toLowerCase())) {
            return null;
        }
        
    	Group newGroup = new Group(this, groupName);
        this.addGroup(newGroup);
        haveGroupsChanged = true;
        return newGroup;
    }

    /**
     *
     * @return a collection of the groups
     */
    public Collection<Group> getGroupList() {
        return groups.values();
    }

    /**
     *
     * @return a collection of the users
     */
    public Collection<User> getUserList() {
        return users.values();
    }

    /**
     * reads the file again
     */
    public void reload() {
        try {
        	reloadGroups();
        	reloadUsers();
        } catch (Exception ex) {
            Logger.getLogger(WorldDataHolder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Refresh Group data from file
     */
    public void reloadGroups() {
    	GroupManager.setLoaded(false);
    	try {
    		// temporary holder in case the load fails.
    		WorldDataHolder ph = new WorldDataHolder(this.getName());

    		loadGroups(ph, getGroupsFile());
    		// transfer new data
    		resetGroups();
    		for (Group tempGroup : ph.getGroupList()) {
    			tempGroup.clone(this);
    		}
    		this.setDefaultGroup(this.getGroup(ph.getDefaultGroup().getName()));
    		this.removeGroupsChangedFlag();
    		this.timeStampGroups = ph.getTimeStampGroups();
    		
    		ph = null;
    	} catch (Exception ex) {
            Logger.getLogger(WorldDataHolder.class.getName()).log(Level.WARNING, null, ex);
        }
    	GroupManager.setLoaded(true);
    }
    
    /**
     * Refresh Users data from file
     */
    public void reloadUsers() {
    	GroupManager.setLoaded(false);
    	try {
    		// temporary holder in case the load fails.
    		WorldDataHolder ph = new WorldDataHolder(this.getName());
    		// copy groups for reference
    		for (Group tempGroup : this.getGroupList()) {
    			tempGroup.clone(ph);
    		}
    		// setup the default group before loading user data.
    		ph.setDefaultGroup(ph.getGroup(this.getDefaultGroup().getName()));
    		loadUsers(ph, getUsersFile());
    		// transfer new data
    		resetUsers();
    		for (User tempUser : ph.getUserList()) {
    			tempUser.clone(this);
    		}
    		this.removeUsersChangedFlag();
    		this.timeStampUsers = ph.getTimeStampUsers();
    		
    		ph = null;
    	} catch (Exception ex) {
            Logger.getLogger(WorldDataHolder.class.getName()).log(Level.WARNING, null, ex);
        } 
    	GroupManager.setLoaded(true);
    }

    /**
     * Save by yourself!
     * @deprecated
     */
    @Deprecated
    public void commit() {
        writeGroups(this, getGroupsFile());
        writeUsers(this, getUsersFile());
    }

    /**
     * Returns a data holder for the given file
     * 
     * @param worldName
     * @param file
     * @return a new WorldDataHolder
     * 
     * @throws Exception
     * @deprecated
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Deprecated
    public static WorldDataHolder load(String worldName, File file) throws Exception {
        WorldDataHolder ph = new WorldDataHolder(worldName);
        ph.f = file;
        final Yaml yaml = new Yaml(new SafeConstructor());
        Map<String, Object> rootDataNode;
        if (!file.exists()) {
            throw new Exception("The file which should contain permissions does not exist!\n" + file.getPath());
        }
        FileInputStream rx = new FileInputStream(file);
        try {
            rootDataNode = (Map<String, Object>) yaml.load(new UnicodeReader(rx));
            if (rootDataNode == null) {
                throw new NullPointerException();
            }
        } catch (Exception ex) {
            throw new Exception("The following file couldn't pass on Parser.\n" + file.getPath(), ex);
        } finally {
            rx.close();
        }
        Map<String, List<String>> inheritance = new HashMap<String, List<String>>();
        try {
            Map<String, Object> allGroupsNode = (Map<String, Object>) rootDataNode.get("groups");
            for (String groupKey : allGroupsNode.keySet()) {
                Map<String, Object> thisGroupNode = (Map<String, Object>) allGroupsNode.get(groupKey);
                Group thisGrp = ph.createGroup(groupKey);
                if (thisGrp == null) {
                    throw new IllegalArgumentException("I think this group was declared more than once: " + groupKey);
                }
                if (thisGroupNode.get("default") == null) {
                    thisGroupNode.put("default", false);
                }
                if ((Boolean.parseBoolean(thisGroupNode.get("default").toString()))) {
                    if (ph.getDefaultGroup() != null) {
                        GroupManager.logger.warning("The group " + thisGrp.getName() + " is declaring be default where" + ph.getDefaultGroup().getName() + " already was.");
                        GroupManager.logger.warning("Overriding first request.");
                    }
                    ph.setDefaultGroup(thisGrp);
                }

                //PERMISSIONS NODE
                if (thisGroupNode.get("permissions") == null) {
                    thisGroupNode.put("permissions", new ArrayList<String>());
                }
                if (thisGroupNode.get("permissions") instanceof List) {
                    for (Object o : ((List) thisGroupNode.get("permissions"))) {
                        thisGrp.addPermission(o.toString());
                    }
                } else if (thisGroupNode.get("permissions") instanceof String) {
                    thisGrp.addPermission((String) thisGroupNode.get("permissions"));
                } else {
                    throw new IllegalArgumentException("Unknown type of permissions node(Should be String or List<String>): " + thisGroupNode.get("permissions").getClass().getName());
                }

                //INFO NODE
                Map<String, Object> infoNode = (Map<String, Object>) thisGroupNode.get("info");
                if (infoNode != null) {
                    thisGrp.setVariables(infoNode);
                }

                //END INFO NODE

                Object inheritNode = thisGroupNode.get("inheritance");
                if (inheritNode == null) {
                    thisGroupNode.put("inheritance", new ArrayList<String>());
                } else if (inheritNode instanceof List) {
                    List<String> groupsInh = (List<String>) inheritNode;
                    for (String grp : groupsInh) {
                        if (inheritance.get(groupKey) == null) {
                            List<String> thisInherits = new ArrayList<String>();
                            inheritance.put(groupKey, thisInherits);
                        }
                        inheritance.get(groupKey).add(grp);

                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Your Permissions config file is invalid. See console for details.");
        }
        if (ph.defaultGroup == null) {
            throw new IllegalArgumentException("There was no Default Group declared.");
        }
        for (String groupKey : inheritance.keySet()) {
            List<String> inheritedList = inheritance.get(groupKey);
            Group thisGroup = ph.getGroup(groupKey);
            for (String inheritedKey : inheritedList) {
                Group inheritedGroup = ph.getGroup(inheritedKey);
                if (thisGroup != null && inheritedGroup != null) {
                    thisGroup.addInherits(inheritedGroup);
                }
            }
        }
        // Process USERS
        Map<String, Object> allUsersNode = (Map<String, Object>) rootDataNode.get("users");
        for (String usersKey : allUsersNode.keySet()) {
            Map<String, Object> thisUserNode = (Map<String, Object>) allUsersNode.get(usersKey);
            User thisUser = ph.createUser(usersKey);
            if (thisUser == null) {
                GroupManager.logger.warning("I think this user was declared more than once: " + usersKey);
                continue;
            }
            if (thisUserNode.get("permissions") == null) {
                thisUserNode.put("permissions", new ArrayList<String>());
            }
            if (thisUserNode.get("permissions") instanceof List) {
                for (Object o : ((List) thisUserNode.get("permissions"))) {
                    thisUser.addPermission(o.toString());
                }
            } else if (thisUserNode.get("permissions") instanceof String) {
                thisUser.addPermission(thisUserNode.get("permissions").toString());
            }


            //USER INFO NODE - BETA

            //INFO NODE
            Map<String, Object> infoNode = (Map<String, Object>) thisUserNode.get("info");
            if (infoNode != null) {
                thisUser.setVariables(infoNode);
            }
            //END INFO NODE - BETA

            if (thisUserNode.get("group") != null) {
                Group hisGroup = ph.getGroup(thisUserNode.get("group").toString());
                if (hisGroup == null) {
                    GroupManager.logger.warning("There is no group " + thisUserNode.get("group").toString() + ", as stated for player " + thisUser.getName());
                    thisUser.setGroup(ph.defaultGroup);
                }
                thisUser.setGroup(hisGroup);
            } else {
                thisUser.setGroup(ph.defaultGroup);
            }
        }
        return ph;
    }

    /**
     * Returns a NEW data holder containing data read from the files
     * 
     * @param worldName
     * @param groupsFile
     * @param usersFile
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static WorldDataHolder load(String worldName, File groupsFile, File usersFile) throws FileNotFoundException, IOException {
    	WorldDataHolder ph = new WorldDataHolder(worldName);
    	
    	GroupManager.setLoaded(false);
    	loadGroups(ph, groupsFile);
    	loadUsers(ph, usersFile);
    	GroupManager.setLoaded(true);
    	
    	return ph;
    }
    
    /**
     * Updates the WorldDataHolder from the Groups file
     * 
     * @param ph
     * @param groupsFile
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected static void loadGroups(WorldDataHolder ph, File groupsFile) throws FileNotFoundException, IOException {

        //READ GROUPS FILE
        Yaml yamlGroups = new Yaml(new SafeConstructor());
        Map<String, Object> groupsRootDataNode;
        if (!groupsFile.exists()) {
            throw new IllegalArgumentException("The file which should contain groups does not exist!\n" + groupsFile.getPath());
        }
        FileInputStream groupsInputStream = new FileInputStream(groupsFile);
        try {
            groupsRootDataNode = (Map<String, Object>) yamlGroups.load(new UnicodeReader(groupsInputStream));
            if (groupsRootDataNode == null) {
                throw new NullPointerException();
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("The following file couldn't pass on Parser.\n" + groupsFile.getPath(), ex);
        } finally {
            groupsInputStream.close();
        }

        //PROCESS GROUPS FILE
        Map<String, List<String>> inheritance = new HashMap<String, List<String>>();
        //try {
            Map<String, Object> allGroupsNode = (Map<String, Object>) groupsRootDataNode.get("groups");
            for (String groupKey : allGroupsNode.keySet()) {
                Map<String, Object> thisGroupNode = (Map<String, Object>) allGroupsNode.get(groupKey);
                Group thisGrp = ph.createGroup(groupKey);
                if (thisGrp == null) {
                    throw new IllegalArgumentException("I think this user was declared more than once: " + groupKey);
                }
                if (thisGroupNode.get("default") == null) {
                    thisGroupNode.put("default", false);
                }
                if ((Boolean.parseBoolean(thisGroupNode.get("default").toString()))) {
                    if (ph.getDefaultGroup() != null) {
                        GroupManager.logger.warning("The group " + thisGrp.getName() + " is claiming to be default where" + ph.getDefaultGroup().getName() + " already was.");
                        GroupManager.logger.warning("Overriding first request.");
                    }
                    ph.setDefaultGroup(thisGrp);
                }

                //PERMISSIONS NODE
                if (thisGroupNode.get("permissions") == null) {
                    thisGroupNode.put("permissions", new ArrayList<String>());
                }
                if (thisGroupNode.get("permissions") instanceof List) {
                    for (Object o : ((List) thisGroupNode.get("permissions"))) {
                        thisGrp.addPermission(o.toString());
                    }
                } else if (thisGroupNode.get("permissions") instanceof String) {
                    thisGrp.addPermission((String) thisGroupNode.get("permissions"));
                } else {
                    throw new IllegalArgumentException("Unknown type of permissions node(Should be String or List<String>) for group:  " + thisGrp.getName());
                }

                //INFO NODE
                if (thisGroupNode.get("info") instanceof Map) {
	                Map<String, Object> infoNode = (Map<String, Object>) thisGroupNode.get("info");
	                if (infoNode != null) {
	                    thisGrp.setVariables(infoNode);
	                }
                } else
                	throw new IllegalArgumentException("Unknown entry found in Info section for group: " + thisGrp.getName());
                	

                //END INFO NODE

                if (thisGroupNode.get("inheritance") == null || thisGroupNode.get("inheritance") instanceof List) {
	                Object inheritNode = thisGroupNode.get("inheritance");
	                if (inheritNode == null) {
	                    thisGroupNode.put("inheritance", new ArrayList<String>());
	                } else if (inheritNode instanceof List) {
	                    List<String> groupsInh = (List<String>) inheritNode;
	                    for (String grp : groupsInh) {
	                        if (inheritance.get(groupKey) == null) {
	                            List<String> thisInherits = new ArrayList<String>();
	                            inheritance.put(groupKey, thisInherits);
	                        }
	                        inheritance.get(groupKey).add(grp);
	
	                    }
	                }
                }else
                	throw new IllegalArgumentException("Unknown entry found in inheritance section for group: " + thisGrp.getName());
            }
        //} catch (Exception ex) {
        //    ex.printStackTrace();
        //    throw new IllegalArgumentException("Your Permissions config file is invalid. See console for details.");
        //}
        if (ph.defaultGroup == null) {
            throw new IllegalArgumentException("There was no Default Group declared.");
        }
        for (String groupKey : inheritance.keySet()) {
            List<String> inheritedList = inheritance.get(groupKey);
            Group thisGroup = ph.getGroup(groupKey);
            for (String inheritedKey : inheritedList) {
                Group inheritedGroup = ph.getGroup(inheritedKey);
                if (thisGroup != null && inheritedGroup != null) {
                    thisGroup.addInherits(inheritedGroup);
                }
            }
        }
        
        ph.removeGroupsChangedFlag();
        // Update the LastModified time.
        ph.groupsFile = groupsFile;
        ph.setTimeStampGroups(groupsFile.lastModified());

        //return ph;
    }
    
    /**
     * Updates the WorldDataHolder from the Users file
     * 
     * @param ph
     * @param usersFile
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected static void loadUsers(WorldDataHolder ph, File usersFile) throws FileNotFoundException, IOException {
    	
        //READ USERS FILE
        Yaml yamlUsers = new Yaml(new SafeConstructor());
        Map<String, Object> usersRootDataNode;
        if (!usersFile.exists()) {
            throw new IllegalArgumentException("The file which should contain users does not exist!\n" + usersFile.getPath());
        }
        FileInputStream usersInputStream = new FileInputStream(usersFile);
        try {
            usersRootDataNode = (Map<String, Object>) yamlUsers.load(new UnicodeReader(usersInputStream));
            if (usersRootDataNode == null) {
                throw new NullPointerException();
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("The following file couldn't pass on Parser.\n" + usersFile.getPath(), ex);
        } finally {
            usersInputStream.close();
        }

        // PROCESS USERS FILE
        Map<String, Object> allUsersNode = (Map<String, Object>) usersRootDataNode.get("users");
        
        // Stop loading if the file is empty
        if (allUsersNode == null)
        	return ;

        for (String usersKey : allUsersNode.keySet()) {
            Map<String, Object> thisUserNode = (Map<String, Object>) allUsersNode.get(usersKey);
            User thisUser = ph.createUser(usersKey);
            if (thisUser == null) {
                throw new IllegalArgumentException("I think this user was declared more than once: " + usersKey);
            }
            if (thisUserNode.get("permissions") == null) {
                thisUserNode.put("permissions", new ArrayList<String>());
            }
            if (thisUserNode.get("permissions") instanceof List) {
                for (Object o : ((List) thisUserNode.get("permissions"))) {
                    thisUser.addPermission(o.toString());
                }
            } else if (thisUserNode.get("permissions") instanceof String) {
                thisUser.addPermission(thisUserNode.get("permissions").toString());
            }

            //SUBGROUPS LOADING
            if (thisUserNode.get("subgroups") == null) {
                thisUserNode.put("subgroups", new ArrayList<String>());
            }
            if (thisUserNode.get("subgroups") instanceof List) {
                for (Object o : ((List) thisUserNode.get("subgroups"))) {
                    Group subGrp = ph.getGroup(o.toString());
                    if (subGrp != null) {
                        thisUser.addSubGroup(subGrp);
                    } else {
                        GroupManager.logger.warning("Subgroup " + o.toString() + " not found for user " + thisUser.getName() + ". Ignoring entry.");
                    }
                }
            } else if (thisUserNode.get("subgroups") instanceof String) {
                Group subGrp = ph.getGroup(thisUserNode.get("subgroups").toString());
                if (subGrp != null) {
                    thisUser.addSubGroup(subGrp);
                } else {
                    GroupManager.logger.warning("Subgroup " + thisUserNode.get("subgroups").toString() + " not found for user " + thisUser.getName() + ". Ignoring entry.");
                }
            }


            //USER INFO NODE - BETA

            //INFO NODE
            Map<String, Object> infoNode = (Map<String, Object>) thisUserNode.get("info");
            if (infoNode != null) {
                thisUser.setVariables(infoNode);
            }
            //END INFO NODE - BETA

            if (thisUserNode.get("group") != null) {
                Group hisGroup = ph.getGroup(thisUserNode.get("group").toString());
                if (hisGroup == null) {
                	GroupManager.logger.warning("There is no group " + thisUserNode.get("group").toString() + ", as stated for player " + thisUser.getName() + ": Set to '" + ph.getDefaultGroup().getName() + "'.");
                	hisGroup = ph.defaultGroup;
                    //throw new IllegalArgumentException("There is no group " + thisUserNode.get("group").toString() + ", as stated for player " + thisUser.getName());
                }
                thisUser.setGroup(hisGroup);
            } else {
                thisUser.setGroup(ph.defaultGroup);
            }
        }
        
        ph.removeUsersChangedFlag();
        // Update the LastModified time.
        ph.usersFile = usersFile;
        ph.setTimeStampUsers(usersFile.lastModified());
        
        //return ph;
    }

    /**
     * Write a dataHolder in a specified file
     * @param ph
     * @param file
     * @deprecated
     */
    @Deprecated
    public static void write(WorldDataHolder ph, File file) {
        Map<String, Object> root = new HashMap<String, Object>();

        Map<String, Object> pluginMap = new HashMap<String, Object>();
        root.put("plugin", pluginMap);

        Map<String, Object> permissionsMap = new HashMap<String, Object>();
        pluginMap.put("permissions", permissionsMap);

        permissionsMap.put("system", "default");

        Map<String, Object> groupsMap = new HashMap<String, Object>();
        root.put("groups", groupsMap);
        for (String groupKey : ph.groups.keySet()) {
            Group group = ph.groups.get(groupKey);

            Map<String, Object> aGroupMap = new HashMap<String, Object>();
            groupsMap.put(group.getName(), aGroupMap);

            aGroupMap.put("default", group.equals(ph.defaultGroup));

            Map<String, Object> infoMap = new HashMap<String, Object>();
            aGroupMap.put("info", infoMap);

            for (String infoKey : group.getVariables().getVarKeyList()) {
                infoMap.put(infoKey, group.getVariables().getVarObject(infoKey));
            }

            aGroupMap.put("inheritance", group.getInherits());

            aGroupMap.put("permissions", group.getPermissionList());
        }

        Map<String, Object> usersMap = new HashMap<String, Object>();
        root.put("users", usersMap);
        for (String userKey : ph.users.keySet()) {
            User user = ph.users.get(userKey);
            if ((user.getGroup() == null || user.getGroup().equals(ph.defaultGroup)) && user.getPermissionList().isEmpty()) {
                continue;
            }

            Map<String, Object> aUserMap = new HashMap<String, Object>();
            usersMap.put(user.getName(), aUserMap);

            if (user.getGroup() == null) {
                aUserMap.put("group", ph.defaultGroup.getName());
            } else {
                aUserMap.put("group", user.getGroup().getName());
            }
            //USER INFO NODE - BETA
            if (user.getVariables().getSize() > 0) {
                Map<String, Object> infoMap = new HashMap<String, Object>();
                aUserMap.put("info", infoMap);

                for (String infoKey : user.getVariables().getVarKeyList()) {
                    infoMap.put(infoKey, user.getVariables().getVarObject(infoKey));
                }
            }
            //END USER INFO NODE - BETA

            aUserMap.put("permissions", user.getPermissionList());
        }
        DumperOptions opt = new DumperOptions();
        opt.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        final Yaml yaml = new Yaml(opt);

        FileWriter tx = null;
        try {
            tx = new FileWriter(file, false);
            tx.write(yaml.dump(root));
            tx.flush();
        } catch (Exception e) {
        } finally {
            try {
                tx.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Write a dataHolder in a specified file
     * @param ph
     * @param groupsFile
     */
    public static void writeGroups(WorldDataHolder ph, File groupsFile) {
        Map<String, Object> root = new HashMap<String, Object>();

        Map<String, Object> groupsMap = new HashMap<String, Object>();
        root.put("groups", groupsMap);
        for (String groupKey : ph.groups.keySet()) {
            Group group = ph.groups.get(groupKey);

            Map<String, Object> aGroupMap = new HashMap<String, Object>();
            groupsMap.put(group.getName(), aGroupMap);

            if (ph.defaultGroup == null) {
                GroupManager.logger.severe("There is no default group for world: " + ph.getName());
            }
            aGroupMap.put("default", group.equals(ph.defaultGroup));

            Map<String, Object> infoMap = new HashMap<String, Object>();
            aGroupMap.put("info", infoMap);

            for (String infoKey : group.getVariables().getVarKeyList()) {
                infoMap.put(infoKey, group.getVariables().getVarObject(infoKey));
            }

            aGroupMap.put("inheritance", group.getInherits());

            aGroupMap.put("permissions", group.getPermissionList());
        }

        if (!root.isEmpty()) {
	        DumperOptions opt = new DumperOptions();
	        opt.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	        final Yaml yaml = new Yaml(opt);
	        try {
	            yaml.dump(root, new OutputStreamWriter(new FileOutputStream(groupsFile), "UTF-8"));
	        } catch (UnsupportedEncodingException ex) {
	        } catch (FileNotFoundException ex) {
	        }
        }
        
        // Update the LastModified time.
        ph.groupsFile = groupsFile;
        ph.setTimeStampGroups(groupsFile.lastModified());
        ph.removeGroupsChangedFlag();

        /*FileWriter tx = null;
        try {
        tx = new FileWriter(groupsFile, false);
        tx.write(yaml.dump(root));
        tx.flush();
        } catch (Exception e) {
        } finally {
        try {
        tx.close();
        } catch (IOException ex) {
        }
        }*/
    }

    /**
     * Write a dataHolder in a specified file
     * @param ph
     * @param usersFile
     */
    public static void writeUsers(WorldDataHolder ph, File usersFile) {
        Map<String, Object> root = new HashMap<String, Object>();

        Map<String, Object> usersMap = new HashMap<String, Object>();
        root.put("users", usersMap);
        for (String userKey : ph.users.keySet()) {
            User user = ph.users.get(userKey);
            if ((user.getGroup() == null || user.getGroup().equals(ph.defaultGroup)) && user.getPermissionList().isEmpty() && user.getVariables().isEmpty() && user.isSubGroupsEmpty()) {
                continue;
            }

            Map<String, Object> aUserMap = new HashMap<String, Object>();
            usersMap.put(user.getName(), aUserMap);

            if (user.getGroup() == null) {
                aUserMap.put("group", ph.defaultGroup.getName());
            } else {
                aUserMap.put("group", user.getGroup().getName());
            }
            //USER INFO NODE - BETA
            if (user.getVariables().getSize() > 0) {
                Map<String, Object> infoMap = new HashMap<String, Object>();
                aUserMap.put("info", infoMap);
                for (String infoKey : user.getVariables().getVarKeyList()) {
                    infoMap.put(infoKey, user.getVariables().getVarObject(infoKey));
                }
            }
            //END USER INFO NODE - BETA
            aUserMap.put("permissions", user.getPermissionList());

            //SUBGROUPS NODE - BETA
            aUserMap.put("subgroups", user.subGroupListStringCopy());
            //END SUBGROUPS NODE - BETA
        }
        
        if (!root.isEmpty()) {
	        DumperOptions opt = new DumperOptions();
	        opt.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	        final Yaml yaml = new Yaml(opt);
	        try {
	            yaml.dump(root, new OutputStreamWriter(new FileOutputStream(usersFile), "UTF-8"));
	        } catch (UnsupportedEncodingException ex) {
	        } catch (FileNotFoundException ex) {
	        }
        }
        
        // Update the LastModified time.
        ph.usersFile = usersFile;
        ph.setTimeStampUsers(usersFile.lastModified());
        ph.removeUsersChangedFlag();
        
        /*FileWriter tx = null;
        try {
        tx = new FileWriter(usersFile, false);
        tx.write(yaml.dump(root));
        tx.flush();
        } catch (Exception e) {
        } finally {
        try {
        tx.close();
        } catch (IOException ex) {
        }
        }*/
    }

    /**
     * Don't use this. Unless you want to make this plugin to interact with original Nijikokun Permissions
     * This method is supposed to make the original one reload the file, and propagate the changes made here.
     *
     * Prefer to use the AnjoCaido's fake version of Nijikokun's Permission plugin.
     * The AnjoCaido's Permission can propagate the changes made on this plugin instantly,
     * without need to save the file.
     *
     * @param server the server that holds the plugin
     * @deprecated it is not used anymore... unless if you use original Permissions
     */
    @Deprecated
    public static void reloadOldPlugins(Server server) {
        // Only reload permissions
        PluginManager pm = server.getPluginManager();
        Plugin[] plugins = pm.getPlugins();
        for (int i = 0; i < plugins.length; i++) {
            plugins[i].getConfiguration().load();
            try {
                plugins[i].getClass().getMethod("setupPermissions").invoke(plugins[i]);
            } catch (Exception ex) {
                continue;
            }
        }
    }

    /**
     * @return the permissionsHandler
     */
    public AnjoPermissionsHandler getPermissionsHandler() {
        if (permissionsHandler == null) {
            permissionsHandler = new AnjoPermissionsHandler(this);
        }
        return permissionsHandler;
    }

    /**
     *
     * @return true if any user data has changed
     */
    public boolean haveUsersChanged() {
        if (haveUsersChanged) {
            return true;
        }
        for (User u : users.values()) {
            if (u.isChanged()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return true if any group data has changed.
     */
    public boolean haveGroupsChanged() {
        if (haveGroupsChanged) {
            return true;
        }
        for (Group g : groups.values()) {
            if (g.isChanged()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public void removeUsersChangedFlag() {
        haveUsersChanged = false;
        for (User u : users.values()) {
            u.flagAsSaved();
        }
    }

    /**
     *
     */
    public void removeGroupsChangedFlag() {
        haveGroupsChanged = false;
        for (Group g : groups.values()) {
            g.flagAsSaved();
        }
    }

    /**
     * @return the usersFile
     */
    public File getUsersFile() {
        return usersFile;
    }

    /**
     * @return the groupsFile
     */
    public File getGroupsFile() {
        return groupsFile;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
	 * Resets Groups.
	 */
	public void resetGroups() {
		this.defaultGroup = null;
		this.groups = new HashMap<String, Group>();
	}
    /**
	 * Resets Users
	 */
	public void resetUsers() {
		this.users = new HashMap<String, User>();
	}
    
    /**
	 * @return the groups
	 */
	public Map<String, Group> getGroups() {
		return groups;
	}
    /**
	 * @return the users
	 */
	public Map<String, User> getUsers() {
		return users;
	}
    
    /**
	 * @return the timeStampGroups
	 */
	public long getTimeStampGroups() {
		return timeStampGroups;
	}
    /**
	 * @return the timeStampUsers
	 */
	public long getTimeStampUsers() {
		return timeStampUsers;
	}

	/**
	 * @param timeStampGroups the timeStampGroups to set
	 */
	protected void setTimeStampGroups(long timeStampGroups) {
		this.timeStampGroups = timeStampGroups;
	}
    /**
	 * @param timeStampUsers the timeStampUsers to set
	 */
	protected void setTimeStampUsers(long timeStampUsers) {
		this.timeStampUsers = timeStampUsers;
	}
	
	public void setTimeStamps() {
		if (groupsFile != null)
			setTimeStampGroups(groupsFile.lastModified());
		if (usersFile != null)
			setTimeStampUsers(usersFile.lastModified());
	}
}
