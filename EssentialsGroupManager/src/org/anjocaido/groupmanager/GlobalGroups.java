package org.anjocaido.groupmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.utils.PermissionCheckResult;
import org.anjocaido.groupmanager.utils.Tasks;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;



/**
 * @author ElgarL
 * 
 */
public class GlobalGroups {

	private GroupManager plugin;
	private YamlConfiguration GGroups;

	private Map<String, Group> groups;
	/**
    *
    */
   protected boolean haveGroupsChanged = false;

	public GlobalGroups(GroupManager plugin) {
		this.plugin = plugin;
		load();
	}

	/**
	 * @return the haveGroupsChanged
	 */
	public boolean haveGroupsChanged() {
        if (this.haveGroupsChanged) {
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
	 * @param haveGroupsChanged the haveGroupsChanged to set
	 */
	public void setGroupsChanged(boolean haveGroupsChanged) {
		this.haveGroupsChanged = haveGroupsChanged;
	}

	@SuppressWarnings("unchecked")
	public void load() {
		
		GGroups = new YamlConfiguration();
		groups = new HashMap<String, Group>();

		// READ globalGroups FILE
		File GlobalGroupsFile = new File(plugin.getDataFolder(),
				"globalgroups.yml");

		if (!GlobalGroupsFile.exists()) {
			try {
				// Create a new file if it doesn't exist.
				Tasks.copy(plugin.getResourceAsStream("globalgroups.yml"),
						GlobalGroupsFile);
			} catch (IOException ex) {
				GroupManager.logger.log(Level.SEVERE, null, ex);
			}
		}

		try {
			GGroups.load(GlobalGroupsFile);
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					"The following file couldn't pass on Parser.\n"
							+ GlobalGroupsFile.getPath(), ex);
		}

		// Read all global groups
		Map<String, Object> allGroups = (Map<String, Object>) GGroups
				.getConfigurationSection("groups").getValues(false);

		// Load each groups permissions list.
		if (allGroups != null)
			for (String groupName : allGroups.keySet()) {
				Group newGroup = new Group(groupName.toLowerCase());
				Object permissions = GGroups.get("groups." + groupName
						+ ".permissions");

				if (permissions instanceof List) {
					for (String permission : (List<String>) permissions) {
						newGroup.addPermission(permission);
					}
				} else if (permissions instanceof String) {
					newGroup.addPermission((String) permissions);
				}

				// Push a new group
				addGroup(newGroup);
			}
		
		GlobalGroupsFile = null;

	}
	
	/**
     * Write a dataHolder in a specified file
     * @param ph
     * @param groupsFile
     */
	
    public void writeGroups() {
    	
    	File GlobalGroupsFile = new File(plugin.getDataFolder(),
				"globalgroups.yml");
    	
        Map<String, Object> root = new HashMap<String, Object>();

        Map<String, Object> groupsMap = new HashMap<String, Object>();
        root.put("groups", groupsMap);
        for (String groupKey : groups.keySet()) {
            Group group = groups.get(groupKey);

            Map<String, Object> aGroupMap = new HashMap<String, Object>();
            groupsMap.put(group.getName(), aGroupMap);

            aGroupMap.put("permissions", group.getPermissionList());
        }

        if (!root.isEmpty()) {
	        DumperOptions opt = new DumperOptions();
	        opt.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	        final Yaml yaml = new Yaml(opt);
	        try {
	            yaml.dump(root, new OutputStreamWriter(new FileOutputStream(GlobalGroupsFile), "UTF-8"));
	        } catch (UnsupportedEncodingException ex) {
	        } catch (FileNotFoundException ex) {
	        }
        }

    }
    
    /**
     * Add a new group if it doesn't already exist.
     * 
     * @param newGroup
     */
    public Group addGroup(Group newGroup) {
    	// Push a new group
		if (!groups.containsKey(newGroup.getName().toLowerCase())) {
			groups.put(newGroup.getName().toLowerCase(), newGroup);
			this.setGroupsChanged(true);
			return newGroup;
		}
		return null;
    }
    
    /**
     * Delete a group if it exist.
     * 
     * @param newGroup
     */
    public boolean removeGroup(String groupName) {
    	// Push a new group
		if (groups.containsKey(groupName.toLowerCase())) {
			groups.remove(groupName.toLowerCase());
			this.setGroupsChanged(true);
			return true;
		}
		return false;
    }
    

	/**
	 * Returns true if the Global Group exists in the globalgroups.yml
	 * 
	 * @param groupName
	 * @return
	 */
	public boolean hasGroup(String groupName) {
		return groups.containsKey(groupName.toLowerCase());
	}

	/**
	 * Returns true if the group has the correct permission node.
	 * 
	 * @param groupName
	 * @param permissionNode
	 * @return
	 */
	public boolean hasPermission(String groupName, String permissionNode) {

		if (!hasGroup(groupName.toLowerCase()))
			return false;

		return groups.get(groupName.toLowerCase()).hasSamePermissionNode(
				permissionNode);

	}

	/**
	 * Returns a PermissionCheckResult of the permission node for the group to
	 * be tested against.
	 * 
	 * @param groupName
	 * @param permissionNode
	 * @return
	 */
	public PermissionCheckResult checkPermission(String groupName,
			String permissionNode) {

		PermissionCheckResult result = new PermissionCheckResult();
		result.askedPermission = permissionNode;
		result.resultType = PermissionCheckResult.Type.NOTFOUND;

		if (!hasGroup(groupName.toLowerCase()))
			return result;

		Group tempGroup = groups.get(groupName.toLowerCase());

		if (tempGroup.hasSamePermissionNode(permissionNode))
			result.resultType = PermissionCheckResult.Type.FOUND;
		if (tempGroup.hasSamePermissionNode("-" + permissionNode))
			result.resultType = PermissionCheckResult.Type.NEGATION;
		if (tempGroup.hasSamePermissionNode("+" + permissionNode))
			result.resultType = PermissionCheckResult.Type.EXCEPTION;

		return result;
	}

	/**
	 * Returns a List of all permission nodes for this group null if none
	 * 
	 * @param groupName
	 * @return
	 */
	public List<String> getGroupsPermissions(String groupName) {
		if (!hasGroup(groupName.toLowerCase()))
			return null;

		return groups.get(groupName.toLowerCase()).getPermissionList();
	}

	/**
	 * Returns a Set of all global group names.
	 * 
	 * @return
	 */
	public Set<String> getGlobalGroups() {
		return groups.keySet();
	}

	/**
	 * 
	 * @return a collection of the groups
	 */
	public Collection<Group> getGroupList() {
		return groups.values();
	}

	/**
	 * Returns the Global Group or null if it doesn't exist.
	 * 
	 * @param groupName
	 * @return
	 */
	public Group getGroup(String groupName) {
		if (!hasGroup(groupName.toLowerCase()))
			return null;

		return groups.get(groupName.toLowerCase());

	}

}