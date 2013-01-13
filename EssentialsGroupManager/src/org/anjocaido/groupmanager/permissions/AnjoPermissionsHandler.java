/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.permissions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.utils.PermissionCheckResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Everything here maintains the model created by Nijikokun
 * 
 * But implemented to use GroupManager system. Which provides instant changes,
 * without file access.
 * 
 * It holds permissions only for one single world.
 * 
 * @author gabrielcouto, ElgarL
 */
public class AnjoPermissionsHandler extends PermissionsReaderInterface {

	WorldDataHolder ph = null;

	/**
	 * It needs a WorldDataHolder to work with.
	 * 
	 * @param holder
	 */
	public AnjoPermissionsHandler(WorldDataHolder holder) {

		ph = holder;
	}

	/**
	 * A short name method, for permission method.
	 * 
	 * @param player
	 * @param permission
	 * @return true if the player has the permission
	 */
	@Override
	public boolean has(Player player, String permission) {

		return permission(player, permission);
	}

	/**
	 * Checks if a player can use that permission node.
	 * 
	 * @param player
	 * @param permission
	 * @return true if the player has the permission
	 */
	@Override
	public boolean permission(Player player, String permission) {

		return checkUserPermission(ph.getUser(player.getName()).updatePlayer(player), permission);
	}

	/**
	 * Checks if a player can use that permission node.
	 * 
	 * @param playerName
	 * @param permission
	 * @return true if the player has the permission
	 */
	public boolean permission(String playerName, String permission) {

		return checkUserPermission(ph.getUser(playerName), permission);
	}

	/**
	 * Returns the name of the group of that player name.
	 * 
	 * @param userName
	 * @return String of players group name.
	 */
	@Override
	public String getGroup(String userName) {

		return ph.getUser(userName).getGroup().getName();
	}

	/**
	 * Returns All permissions (including inheritance and sub groups) for the
	 * player, including child nodes from Bukkit.
	 * 
	 * @param userName
	 * @return List<String> of all players permissions.
	 */
	@Override
	public List<String> getAllPlayersPermissions(String userName) {

		List<String> perms = new ArrayList<String>();

		perms.addAll(getAllPlayersPermissions(userName, true));

		return perms;
	}

	/**
	 * Returns All permissions (including inheritance and sub groups) for the
	 * player. With or without Bukkit child nodes.
	 * 
	 * @param userName
	 * @return Set<String> of all players permissions.
	 */
	@Override
	public Set<String> getAllPlayersPermissions(String userName, Boolean includeChildren) {

		Set<String> playerPermArray = new HashSet<String>();

		// Add the players own permissions.
		playerPermArray.addAll(populatePerms(ph.getUser(userName).getPermissionList(), includeChildren));

		ArrayList<String> alreadyProcessed = new ArrayList<String>();

		// fetch all group permissions
		for (String group : getGroups(userName)) {
			// Don't process a group more than once.
			if (!alreadyProcessed.contains(group)) {
				alreadyProcessed.add(group);

				Set<String> groupPermArray = new HashSet<String>();

				if (group.startsWith("g:") && GroupManager.getGlobalGroups().hasGroup(group)) {
					// GlobalGroups
					groupPermArray = populatePerms(GroupManager.getGlobalGroups().getGroupsPermissions(group), includeChildren);

				} else {
					// World Groups
					groupPermArray = populatePerms(ph.getGroup(group).getPermissionList(), includeChildren);
				}

				// Add all group permissions, unless negated by earlier permissions.
				for (String perm : groupPermArray) {
					boolean negated = (perm.startsWith("-"));
					// Perm doesn't already exists and there is no negation for it
					// or It's a negated perm where a normal perm doesn't exists (don't allow inheritance to negate higher perms)
					if ((!negated && !playerPermArray.contains(perm) && !playerPermArray.contains("-" + perm)) || (negated && !playerPermArray.contains(perm.substring(1)) && !playerPermArray.contains("-" + perm)))
						playerPermArray.add(perm);
				}
			}

		}
		// Collections.sort(playerPermArray, StringPermissionComparator.getInstance());

		return playerPermArray;
	}

	private Set<String> populatePerms(List<String> permsList, boolean includeChildren) {

		// Create a new array so it's modifiable.
		List<String> perms = new ArrayList<String>(permsList);
		Set<String> permArray = new HashSet<String>();
		Boolean allPerms = false;

		// Allow * node to populate ALL permissions to Bukkit.
		if (perms.contains("*")) {
			permArray.addAll(GroupManager.BukkitPermissions.getAllRegisteredPermissions(includeChildren));
			allPerms = true;
			perms.remove("*");
		}

		for (String perm : perms) {

			/**
			 * all permission sets are passed here pre-sorted, alphabetically.
			 * This means negated nodes will be processed before all permissions
			 * other than *.
			 */
			boolean negated = perm.startsWith("-");

			if (!permArray.contains(perm)) {
				permArray.add(perm);

				if ((negated) && (permArray.contains(perm.substring(1))))
					permArray.remove(perm.substring(1));

				/**
				 * Process child nodes if required,
				 * or this is a negated node AND we used * to include all
				 * permissions,
				 * in which case we need to remove all children of that node.
				 */
				if ((includeChildren) || (negated && allPerms)) {

					Map<String, Boolean> children = GroupManager.BukkitPermissions.getAllChildren((negated ? perm.substring(1) : perm), new HashSet<String>());

					if (children != null) {
						if (negated)
							if (allPerms) {

								// Remove children of negated nodes
								for (String child : children.keySet())
									if (children.get(child))
										if (permArray.contains(child))
											permArray.remove(child);

							} else {

								// Add child nodes
								for (String child : children.keySet())
									if (children.get(child))
										if ((!permArray.contains(child)) && (!permArray.contains("-" + child)))
											permArray.add(child);
							}
					}
				}
			}
		}

		return permArray;
	}

	/**
	 * Verify if player is in such group. It will check it's groups inheritance.
	 * 
	 * So if you have a group Admin > Moderator
	 * 
	 * And verify the player 'MyAdmin', which is Admin, it will return true for
	 * both Admin or Moderator groups.
	 * 
	 * If you have a player 'MyModerator', which is Moderator, it will give
	 * false if you pass Admin in group parameter.
	 * 
	 * @param name
	 * @param group
	 * @return true if in group (with inheritance)
	 */
	@Override
	public boolean inGroup(String name, String group) {

		if (hasGroupInInheritance(ph.getUser(name).getGroup(), group)) {
			return true;
		}
		for (Group subGroup : ph.getUser(name).subGroupListCopy()) {
			if (hasGroupInInheritance(subGroup, group)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the appropriate prefix for the user. This method is a utility method
	 * for chat plugins to get the user's prefix without having to look at every
	 * one of the user's ancestors. Returns an empty string if user has no
	 * parent groups.
	 * 
	 * @param user
	 *            Player's name
	 * @return Player's prefix
	 */
	@Override
	public String getUserPrefix(String user) {

		String prefix = ph.getUser(user).getVariables().getVarString("prefix");
		if (prefix.length() != 0) {
			return prefix;
		}

		return getGroupPrefix(getGroup(user));
	}

	/**
	 * Gets the appropriate prefix for the user. This method is a utility method
	 * for chat plugins to get the user's prefix without having to look at every
	 * one of the user's ancestors. Returns an empty string if user has no
	 * parent groups.
	 * 
	 * @param user
	 *            Player's name
	 * @return Player's prefix
	 */
	@Override
	public String getUserSuffix(String user) {

		String suffix = ph.getUser(user).getVariables().getVarString("suffix");
		if (suffix.length() != 0) {
			return suffix;
		}

		return getGroupSuffix(getGroup(user));

	}

	/**
	 * Gets name of the primary group of the user. Returns the name of the
	 * default group if user has no parent groups, or "Default" if there is no
	 * default group for that world.
	 * 
	 * @param user
	 *            Player's name
	 * @return Name of player's primary group
	 */
	public String getPrimaryGroup(String user) {

		return getGroup(user);

	}

	/**
	 * Check if user can build. Checks inheritance and subgroups.
	 * 
	 * @param userName
	 *            Player's name
	 * @return true if the user can build
	 */
	public boolean canUserBuild(String userName) {

		return getPermissionBoolean(userName, "build");

	}

	/**
	 * Returns the String prefix for the given group
	 * 
	 * @param groupName
	 * @return empty string if found none.
	 */
	@Override
	public String getGroupPrefix(String groupName) {

		Group g = ph.getGroup(groupName);
		if (g == null) {
			return "";
		}
		return g.getVariables().getVarString("prefix");
	}

	/**
	 * Return the suffix for the given group name
	 * 
	 * @param groupName
	 * @return empty string if not found.
	 */
	@Override
	public String getGroupSuffix(String groupName) {

		Group g = ph.getGroup(groupName);
		if (g == null) {
			return "";
		}
		return g.getVariables().getVarString("suffix");
	}

	/**
	 * Checks the specified group for the Info Build node. Does NOT check
	 * inheritance
	 * 
	 * @param groupName
	 * @return true if can build
	 */
	@Override
	public boolean canGroupBuild(String groupName) {

		Group g = ph.getGroup(groupName);
		if (g == null) {
			return false;
		}
		return g.getVariables().getVarBoolean("build");
	}

	/**
	 * It returns a string variable value, set in the INFO node of the group. It
	 * will harvest inheritance for value.
	 * 
	 * @param groupName
	 * @param variable
	 * @return null if no group with that variable is found.
	 */
	@Override
	public String getGroupPermissionString(String groupName, String variable) {

		Group start = ph.getGroup(groupName);
		if (start == null) {
			return null;
		}
		Group result = nextGroupWithVariable(start, variable);
		if (result == null) {
			return null;
		}
		return result.getVariables().getVarString(variable);
	}

	/**
	 * It returns a Integer variable value It will harvest inheritance for
	 * value.
	 * 
	 * @param groupName
	 * @param variable
	 * @return -1 if none found or not parseable.
	 */
	@Override
	public int getGroupPermissionInteger(String groupName, String variable) {

		Group start = ph.getGroup(groupName);
		if (start == null) {
			return -1;
		}
		Group result = nextGroupWithVariable(start, variable);
		if (result == null) {
			return -1;
		}
		return result.getVariables().getVarInteger(variable);
	}

	/**
	 * Returns a boolean for given variable in INFO node. It will harvest
	 * inheritance for value.
	 * 
	 * @param group
	 * @param variable
	 * @return false if not found/not parseable.
	 */
	@Override
	public boolean getGroupPermissionBoolean(String group, String variable) {

		Group start = ph.getGroup(group);
		if (start == null) {
			return false;
		}
		Group result = nextGroupWithVariable(start, variable);
		if (result == null) {
			return false;
		}
		return result.getVariables().getVarBoolean(variable);
	}

	/**
	 * Returns a double value for the given variable name in INFO node. It will
	 * harvest inheritance for value.
	 * 
	 * @param group
	 * @param variable
	 * @return -1 if not found / not parseable.
	 */
	@Override
	public double getGroupPermissionDouble(String group, String variable) {

		Group start = ph.getGroup(group);
		if (start == null) {
			return -1;
		}
		Group result = nextGroupWithVariable(start, variable);
		if (result == null) {
			return -1;
		}
		return result.getVariables().getVarDouble(variable);
	}

	/**
	 * Returns the variable value of the user, in INFO node.
	 * 
	 * @param user
	 * @param variable
	 * @return empty string if not found
	 */
	@Override
	public String getUserPermissionString(String user, String variable) {

		User auser = ph.getUser(user);
		if (auser == null) {
			return "";
		}
		return auser.getVariables().getVarString(variable);
	}

	/**
	 * Returns the variable value of the user, in INFO node.
	 * 
	 * @param user
	 * @param variable
	 * @return -1 if not found
	 */
	@Override
	public int getUserPermissionInteger(String user, String variable) {

		User auser = ph.getUser(user);
		if (auser == null) {
			return -1;
		}
		return auser.getVariables().getVarInteger(variable);
	}

	/**
	 * Returns the variable value of the user, in INFO node.
	 * 
	 * @param user
	 * @param variable
	 * @return boolean value
	 */
	@Override
	public boolean getUserPermissionBoolean(String user, String variable) {

		User auser = ph.getUser(user);
		if (auser == null) {
			return false;
		}
		return auser.getVariables().getVarBoolean(variable);
	}

	/**
	 * Returns the variable value of the user, in INFO node.
	 * 
	 * @param user
	 * @param variable
	 * @return -1 if not found
	 */
	@Override
	public double getUserPermissionDouble(String user, String variable) {

		User auser = ph.getUser(user);
		if (auser == null) {
			return -1;
		}
		return auser.getVariables().getVarDouble(variable);
	}

	/**
	 * Returns the variable value of the user, in INFO node. If not found, it
	 * will search for his Group variables. It will harvest the inheritance and
	 * subgroups.
	 * 
	 * @param user
	 * @param variable
	 * @return empty string if not found
	 */
	@Override
	public String getPermissionString(String user, String variable) {

		User auser = ph.getUser(user);
		if (auser == null) {
			return "";
		}
		if (auser.getVariables().hasVar(variable)) {
			return auser.getVariables().getVarString(variable);
		}
		Group start = auser.getGroup();
		if (start == null) {
			return "";
		}
		Group result = nextGroupWithVariable(start, variable);
		if (result == null) {
			// Check sub groups
			if (!auser.isSubGroupsEmpty())
				for (Group subGroup : auser.subGroupListCopy()) {
					result = nextGroupWithVariable(subGroup, variable);
					// Found value?
					if (result != null)
						continue;
				}
			if (result == null)
				return "";
		}
		return result.getVariables().getVarString(variable);
		// return getUserPermissionString(user, variable);
	}

	/**
	 * Returns the variable value of the user, in INFO node. If not found, it
	 * will search for his Group variables. It will harvest the inheritance and
	 * subgroups.
	 * 
	 * @param user
	 * @param variable
	 * @return -1 if not found
	 */
	@Override
	public int getPermissionInteger(String user, String variable) {

		User auser = ph.getUser(user);
		if (auser == null) {
			return -1;
		}
		if (auser.getVariables().hasVar(variable)) {
			return auser.getVariables().getVarInteger(variable);
		}
		Group start = auser.getGroup();
		if (start == null) {
			return -1;
		}
		Group result = nextGroupWithVariable(start, variable);
		if (result == null) {
			// Check sub groups
			if (!auser.isSubGroupsEmpty())
				for (Group subGroup : auser.subGroupListCopy()) {
					result = nextGroupWithVariable(subGroup, variable);
					// Found value?
					if (result != null)
						continue;
				}
			if (result == null)
				return -1;
		}
		return result.getVariables().getVarInteger(variable);
		// return getUserPermissionInteger(string, string1);
	}

	/**
	 * Returns the variable value of the user, in INFO node. If not found, it
	 * will search for his Group variables. It will harvest the inheritance and
	 * subgroups.
	 * 
	 * @param user
	 * @param variable
	 * @return false if not found or not parseable to true.
	 */
	@Override
	public boolean getPermissionBoolean(String user, String variable) {

		User auser = ph.getUser(user);
		if (auser == null) {
			return false;
		}
		if (auser.getVariables().hasVar(variable)) {
			return auser.getVariables().getVarBoolean(variable);
		}
		Group start = auser.getGroup();
		if (start == null) {
			return false;
		}
		Group result = nextGroupWithVariable(start, variable);
		if (result == null) {
			// Check sub groups
			if (!auser.isSubGroupsEmpty())
				for (Group subGroup : auser.subGroupListCopy()) {
					result = nextGroupWithVariable(subGroup, variable);
					// Found value?
					if (result != null)
						continue;
				}
			if (result == null)
				return false;
		}
		return result.getVariables().getVarBoolean(variable);
		// return getUserPermissionBoolean(user, string1);
	}

	/**
	 * Returns the variable value of the user, in INFO node. If not found, it
	 * will search for his Group variables. It will harvest the inheritance and
	 * subgroups.
	 * 
	 * @param user
	 * @param variable
	 * @return -1 if not found.
	 */
	@Override
	public double getPermissionDouble(String user, String variable) {

		User auser = ph.getUser(user);
		if (auser == null) {
			return -1.0D;
		}
		if (auser.getVariables().hasVar(variable)) {
			return auser.getVariables().getVarDouble(variable);
		}
		Group start = auser.getGroup();
		if (start == null) {
			return -1.0D;
		}
		Group result = nextGroupWithVariable(start, variable);
		if (result == null) {
			// Check sub groups
			if (!auser.isSubGroupsEmpty())
				for (Group subGroup : auser.subGroupListCopy()) {
					result = nextGroupWithVariable(subGroup, variable);
					// Found value?
					if (result != null)
						continue;
				}
			if (result == null)
				return -1.0D;
		}
		return result.getVariables().getVarDouble(variable);
		// return getUserPermissionDouble(string, string1);
	}

	/**
	 * Does not include User's group permission
	 * 
	 * @param user
	 * @param permission
	 * @return PermissionCheckResult
	 */
	public PermissionCheckResult checkUserOnlyPermission(User user, String permission) {

		user.sortPermissions();
		PermissionCheckResult result = new PermissionCheckResult();
		result.askedPermission = permission;
		result.owner = user;
		for (String access : user.getPermissionList()) {
			result.resultType = comparePermissionString(access, permission);
			if (result.resultType != PermissionCheckResult.Type.NOTFOUND) {
				return result;
			}
		}
		result.resultType = PermissionCheckResult.Type.NOTFOUND;
		return result;
	}

	/**
	 * Returns the node responsible for that permission. Does not include User's
	 * group permission.
	 * 
	 * @param group
	 * @param permission
	 * @return the node if permission is found. if not found, return null
	 */
	public PermissionCheckResult checkGroupOnlyPermission(Group group, String permission) {

		group.sortPermissions();
		PermissionCheckResult result = new PermissionCheckResult();
		result.owner = group;
		result.askedPermission = permission;
		for (String access : group.getPermissionList()) {
			result.resultType = comparePermissionString(access, permission);
			if (result.resultType != PermissionCheckResult.Type.NOTFOUND) {
				return result;
			}
		}
		result.resultType = PermissionCheckResult.Type.NOTFOUND;
		return result;
	}

	/**
	 * Check permissions, including it's group and inheritance.
	 * 
	 * @param user
	 * @param permission
	 * @return true if permission was found. false if not, or was negated.
	 */
	public boolean checkUserPermission(User user, String permission) {

		PermissionCheckResult result = checkFullGMPermission(user, permission, true);
		if (result.resultType == PermissionCheckResult.Type.EXCEPTION || result.resultType == PermissionCheckResult.Type.FOUND) {
			return true;
		}

		return false;
	}

	/**
	 * Do what checkUserPermission did before. But now returning a
	 * PermissionCheckResult.
	 * 
	 * @param user
	 * @param targetPermission
	 * @return PermissionCheckResult
	 */
	public PermissionCheckResult checkFullUserPermission(User user, String targetPermission) {

		return checkFullGMPermission(user, targetPermission, true);
	}

	/**
	 * Check user and groups with inheritance and Bukkit if bukkit = true return
	 * a PermissionCheckResult.
	 * 
	 * @param user
	 * @param targetPermission
	 * @param checkBukkit
	 * @return PermissionCheckResult
	 */
	public PermissionCheckResult checkFullGMPermission(User user, String targetPermission, Boolean checkBukkit) {

		PermissionCheckResult result = new PermissionCheckResult();
		result.accessLevel = targetPermission;
		result.resultType = PermissionCheckResult.Type.NOTFOUND;

		if (user == null || targetPermission == null || targetPermission.isEmpty()) {
			return result;
		}
		
		/*
		 * Do not push any perms to bukkit if...
		 * We are in offline mode
		 * and the player has the 'groupmanager.noofflineperms' permission.
		 */
		if (!Bukkit.getServer().getOnlineMode()
				&& (checkFullGMPermission(user, "groupmanager.noofflineperms", true).resultType == PermissionCheckResult.Type.FOUND))
			return result;

		if (checkBukkit) {
			// Check Bukkit perms to support plugins which add perms via code
			// (Heroes).
			final Player player = user.getBukkitPlayer();
			//final Permission bukkitPerm = Bukkit.getPluginManager().getPermission(targetPermission);
			if ((player != null) && player.hasPermission(targetPermission)) {
				result.resultType = PermissionCheckResult.Type.FOUND;
				result.owner = user;
				return result;
			}
		}

		PermissionCheckResult resultUser = checkUserOnlyPermission(user, targetPermission);
		if (resultUser.resultType != PermissionCheckResult.Type.NOTFOUND) {
			resultUser.accessLevel = targetPermission;
			return resultUser;
		}

		// IT ONLY CHECKS GROUPS PERMISSIONS IF RESULT FOR USER IS NOT FOUND
		PermissionCheckResult resultGroup = checkGroupPermissionWithInheritance(user.getGroup(), targetPermission);
		if (resultGroup.resultType != PermissionCheckResult.Type.NOTFOUND) {
			resultGroup.accessLevel = targetPermission;
			return resultGroup;
		}

		// SUBGROUPS CHECK
		for (Group subGroup : user.subGroupListCopy()) {
			PermissionCheckResult resultSubGroup = checkGroupPermissionWithInheritance(subGroup, targetPermission);
			if (resultSubGroup.resultType != PermissionCheckResult.Type.NOTFOUND) {
				resultSubGroup.accessLevel = targetPermission;
				return resultSubGroup;
			}
		}

		// THEN IT RETURNS A NOT FOUND
		return result;
	}

	/**
	 * Returns the next group, including inheritance, which contains that
	 * variable name.
	 * 
	 * It does Breadth-first search
	 * 
	 * @param start the starting group to look for
	 * @param targetVariable the variable name
	 * @return The group if found. Null if not.
	 */
	public Group nextGroupWithVariable(Group start, String targetVariable) {

		if (start == null || targetVariable == null) {
			return null;
		}
		LinkedList<Group> stack = new LinkedList<Group>();
		ArrayList<Group> alreadyVisited = new ArrayList<Group>();
		stack.push(start);
		alreadyVisited.add(start);
		while (!stack.isEmpty()) {
			Group now = stack.pop();
			if (now.getVariables().hasVar(targetVariable)) {
				return now;
			}
			for (String sonName : now.getInherits()) {
				Group son = ph.getGroup(sonName);
				if (son != null && !alreadyVisited.contains(son)) {
					stack.push(son);
					alreadyVisited.add(son);
				}
			}
		}
		return null;
	}


	/**
	 * Check if given group inherits another group.
	 * 
	 * It does Breadth-first search
	 * 
	 * @param start The group to start the search.
	 * @param askedGroup Name of the group you're looking for
	 * @return true if it inherits the group.
	 */
	public boolean hasGroupInInheritance(Group start, String askedGroup) {

		if (start == null || askedGroup == null) {
			return false;
		}
		LinkedList<Group> stack = new LinkedList<Group>();
		ArrayList<Group> alreadyVisited = new ArrayList<Group>();
		stack.push(start);
		alreadyVisited.add(start);
		while (!stack.isEmpty()) {
			Group now = stack.pop();
			if (now.getName().equalsIgnoreCase(askedGroup)) {
				return true;
			}
			for (String sonName : now.getInherits()) {
				Group son = ph.getGroup(sonName);
				if (son != null && !alreadyVisited.contains(son)) {
					stack.push(son);
					alreadyVisited.add(son);
				}
			}
		}
		return false;
	}

	/**
	 * Returns the result of permission check. Including inheritance. If found
	 * anything, the PermissionCheckResult that retuns will include the Group
	 * name, and the result type. Result types will be EXCEPTION, NEGATION,
	 * FOUND.
	 * 
	 * If returned type NOTFOUND, the owner will be null, and ownerType too.
	 * 
	 * It does Breadth-first search
	 * 
	 * @param start
	 * @param targetPermission
	 * @return PermissionCheckResult
	 */
	public PermissionCheckResult checkGroupPermissionWithInheritance(Group start, String targetPermission) {

		if (start == null || targetPermission == null) {
			return null;
		}
		LinkedList<Group> stack = new LinkedList<Group>();
		List<Group> alreadyVisited = new ArrayList<Group>();
		stack.push(start);
		alreadyVisited.add(start);
		while (!stack.isEmpty()) {
			Group now = stack.pop();
			PermissionCheckResult resultNow = checkGroupOnlyPermission(now, targetPermission);
			if (!resultNow.resultType.equals(PermissionCheckResult.Type.NOTFOUND)) {
				resultNow.accessLevel = targetPermission;
				return resultNow;
			}
			for (String sonName : now.getInherits()) {
				Group son = ph.getGroup(sonName);
				if (son != null && !alreadyVisited.contains(son)) {
					// Add rather than push to retain inheritance order.
					stack.add(son);
					alreadyVisited.add(son);
				}
			}
		}
		PermissionCheckResult result = new PermissionCheckResult();
		result.askedPermission = targetPermission;
		result.resultType = PermissionCheckResult.Type.NOTFOUND;
		return result;
	}

	/**
	 * Return whole list of names of groups in a inheritance chain. Including a
	 * starting group.
	 * 
	 * It does Breadth-first search. So closer groups will appear first in list.
	 * 
	 * @param start
	 * @return the group that passed on test. null if no group passed.
	 */
	public ArrayList<String> listAllGroupsInherited(Group start) {

		if (start == null) {
			return null;
		}
		LinkedList<Group> stack = new LinkedList<Group>();
		ArrayList<String> alreadyVisited = new ArrayList<String>();
		stack.push(start);
		alreadyVisited.add(start.getName());
		while (!stack.isEmpty()) {
			Group now = stack.pop();
			for (String sonName : now.getInherits()) {
				Group son = ph.getGroup(sonName);
				if (son != null && !alreadyVisited.contains(son.getName())) {
					stack.push(son);
					alreadyVisited.add(son.getName());
				}
			}
		}
		return alreadyVisited;
	}

	/**
	 * Compare a user permission like 'myplugin.*' against a full plugin
	 * permission name, like 'myplugin.dosomething'. As the example above, will
	 * return true.
	 * 
	 * Please sort permissions before sending them here. So negative tokens get
	 * priority.
	 * 
	 * You must test if it start with negative outside this method. It will only
	 * tell if the nodes are matching or not.
	 * 
	 * Every '-' or '+' in the beginning is ignored. It will match only node
	 * names.
	 * 
	 * @param userAccessLevel
	 * @param fullPermissionName
	 * @return PermissionCheckResult.Type
	 */
	public PermissionCheckResult.Type comparePermissionString(String userAccessLevel, String fullPermissionName) {

		int userAccessLevelLength;
		if (userAccessLevel == null || fullPermissionName == null || fullPermissionName.length() == 0 || (userAccessLevelLength = userAccessLevel.length()) == 0) {
			return PermissionCheckResult.Type.NOTFOUND;
		}

		PermissionCheckResult.Type result = PermissionCheckResult.Type.FOUND;
		int userAccessLevelOffset = 0;
		if (userAccessLevel.charAt(0) == '+') {
			userAccessLevelOffset = 1;
			result = PermissionCheckResult.Type.EXCEPTION;
		} else if (userAccessLevel.charAt(0) == '-') {
			userAccessLevelOffset = 1;
			result = PermissionCheckResult.Type.NEGATION;
		}
		if ("*".regionMatches(0, userAccessLevel, userAccessLevelOffset, userAccessLevelLength - userAccessLevelOffset)) {
			return result;
		}
		int fullPermissionNameOffset;
		if (fullPermissionName.charAt(0) == '+' || fullPermissionName.charAt(0) == '-') {
			fullPermissionNameOffset = 1;
		} else {
			fullPermissionNameOffset = 0;
		}

		if (userAccessLevel.charAt(userAccessLevel.length() - 1) == '*') {
			return userAccessLevel.regionMatches(true, userAccessLevelOffset, fullPermissionName, fullPermissionNameOffset, userAccessLevelLength - userAccessLevelOffset - 1) ? result : PermissionCheckResult.Type.NOTFOUND;
		} else {
			return userAccessLevel.regionMatches(true, userAccessLevelOffset, fullPermissionName, fullPermissionNameOffset, Math.max(userAccessLevelLength - userAccessLevelOffset, fullPermissionName.length() - fullPermissionNameOffset)) ? result : PermissionCheckResult.Type.NOTFOUND;
		}
	}

	/**
	 * Returns a list of all groups.
	 * 
	 * Including subgroups.
	 * 
	 * @param userName
	 * @return String[] of all group names.
	 */
	@Override
	public String[] getGroups(String userName) {

		ArrayList<String> allGroups = listAllGroupsInherited(ph.getUser(userName).getGroup());
		for (Group subg : ph.getUser(userName).subGroupListCopy()) {
			allGroups.addAll(listAllGroupsInherited(subg));
		}

		String[] arr = new String[allGroups.size()];
		return allGroups.toArray(arr);
	}

	/**
	 * A Breadth-first search thru inheritance model.
	 * 
	 * Just a model to copy and paste. This will guarantee the closer groups
	 * will be checked first.
	 * 
	 * @param start
	 * @param targerPermission
	 * @return
	 */
	@SuppressWarnings("unused")
	private Group breadthFirstSearch(Group start, String targerPermission) {

		if (start == null || targerPermission == null) {
			return null;
		}
		LinkedList<Group> stack = new LinkedList<Group>();
		ArrayList<Group> alreadyVisited = new ArrayList<Group>();
		stack.push(start);
		alreadyVisited.add(start);
		while (!stack.isEmpty()) {
			Group now = stack.pop();
			PermissionCheckResult resultNow = checkGroupOnlyPermission(now, targerPermission);
			if (resultNow.resultType.equals(PermissionCheckResult.Type.EXCEPTION) || resultNow.resultType.equals(PermissionCheckResult.Type.FOUND)) {
				return now;
			}
			if (resultNow.resultType.equals(PermissionCheckResult.Type.NEGATION)) {
				return null;
			}
			for (String sonName : now.getInherits()) {
				Group son = ph.getGroup(sonName);
				if (son != null && !alreadyVisited.contains(son)) {
					stack.push(son);
					alreadyVisited.add(son);
				}
			}
		}
		return null;
	}

	@Override
	public Group getDefaultGroup() {

		return ph.getDefaultGroup();
	}

	@Override
	public String getInfoString(String entryName, String path, boolean isGroup) {

		if (isGroup) {
			Group data = ph.getGroup(entryName);
			if (data == null) {
				return null;
			}
			return data.getVariables().getVarString(path);
		} else {
			User data = ph.getUser(entryName);
			if (data == null) {
				return null;
			}
			return data.getVariables().getVarString(path);
		}
	}

	@Override
	public int getInfoInteger(String entryName, String path, boolean isGroup) {

		if (isGroup) {
			Group data = ph.getGroup(entryName);
			if (data == null) {
				return -1;
			}
			return data.getVariables().getVarInteger(path);
		} else {
			User data = ph.getUser(entryName);
			if (data == null) {
				return -1;
			}
			return data.getVariables().getVarInteger(path);
		}
	}

	@Override
	public double getInfoDouble(String entryName, String path, boolean isGroup) {

		if (isGroup) {
			Group data = ph.getGroup(entryName);
			if (data == null) {
				return -1;
			}
			return data.getVariables().getVarDouble(path);
		} else {
			User data = ph.getUser(entryName);
			if (data == null) {
				return -1;
			}
			return data.getVariables().getVarDouble(path);
		}

	}

	@Override
	public boolean getInfoBoolean(String entryName, String path, boolean isGroup) {

		if (isGroup) {
			Group data = ph.getGroup(entryName);
			if (data == null) {
				return false;
			}
			return data.getVariables().getVarBoolean(path);
		} else {
			User data = ph.getUser(entryName);
			if (data == null) {
				return false;
			}
			return data.getVariables().getVarBoolean(path);
		}
	}

	@Override
	public void addUserInfo(String name, String path, Object data) {

		ph.getUser(name).getVariables().addVar(path, data);
	}

	@Override
	public void removeUserInfo(String name, String path) {

		ph.getUser(name).getVariables().removeVar(path);
	}

	@Override
	public void addGroupInfo(String name, String path, Object data) {

		ph.getGroup(name).getVariables().addVar(path, data);
	}

	@Override
	public void removeGroupInfo(String name, String path) {

		ph.getGroup(name).getVariables().removeVar(path);
	}
}
