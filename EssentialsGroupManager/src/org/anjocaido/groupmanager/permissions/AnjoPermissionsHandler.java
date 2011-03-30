/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.permissions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.utils.PermissionCheckResult;
import org.bukkit.entity.Player;

/**
 *  Everything here maintains the model created by Nijikokun
 *
 * But implemented to use GroupManager system. Which provides instant changes,
 * without file access.
 *
 * It holds permissions only for one single world.
 *
 * @author gabrielcouto
 */
public class AnjoPermissionsHandler extends PermissionsReaderInterface {

    WorldDataHolder ph = null;

    /**
     * It needs a WorldDataHolder to work with.
     * @param holder
     */
    public AnjoPermissionsHandler(WorldDataHolder holder) {
        ph = holder;
    }

    /**
     * A short name method, for permission method.
     * @param player
     * @param permission
     * @return
     */
    @Override
    public boolean has(Player player, String permission) {
        return permission(player, permission);
    }

    /**
     *  Checks if a player can use that permission node.
     * @param player
     * @param permission
     * @return
     */
    @Override
    public boolean permission(Player player, String permission) {
        return checkUserPermission(ph.getUser(player.getName()), permission);
    }

    /**
     * Returns the name of the group of that player name.
     * @param userName
     * @return
     */
    @Override
    public String getGroup(String userName) {
        return ph.getUser(userName).getGroup().getName();
    }

    /**
     *  Verify if player is in suck group.
     * It will check it's groups inheritance.
     *
     * So if you have a group Admin > Moderator
     *
     * And verify the player 'MyAdmin', which is Admin, it will return true for both
     * Admin or Moderator groups.
     *
     * Mas if you haave a player 'MyModerator', which is Moderator,
     * it will give false if you pass Admin in group parameter.
     *
     * @param name
     * @param group
     * @return
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
     * Returns the String prefix for the given group
     * @param groupName
     * @return empty string if found none.
     */
    @Override
    public String getGroupPrefix(String groupName) {
        Group g = ph.getGroup(groupName);
        if (g == null) {
            return null;
        }
        return g.getVariables().getVarString("prefix");
    }

    /**
     * Return the suffix for the given group name
     * @param groupName
     * @return
     */
    @Override
    public String getGroupSuffix(String groupName) {
        Group g = ph.getGroup(groupName);
        if (g == null) {
            return null;
        }
        return g.getVariables().getVarString("suffix");
    }

    /**
     *
     * @param groupName
     * @return
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
     * It returns a string variable value, set in the INFO node of the group.
     * It will harvest inheritance for value.
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
     *  It returns a Integer variable value
     * It will harvest inheritance for value.
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
     * Returns a boolean for given variable in INFO node.
     * It will harvest inheritance for value.
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
     * Returns a double value for the given variable name in INFO node.
     * It will harvest inheritance for value.
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
     * @param user
     * @param variable
     * @return
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
     * @param user
     * @param variable
     * @return
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
     * @param user
     * @param variable
     * @return
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
     * @param user
     * @param variable
     * @return
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
     * Returns the variable value of the user, in INFO node.
     * If not found, it will search for his Group variables.
     * It will harvest the inheritance.
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
            return "";
        }
        return result.getVariables().getVarString(variable);
        //return getUserPermissionString(user, variable);
    }

    /**
     * Returns the variable value of the user, in INFO node.
     * If not found, it will search for his Group variables.
     * It will harvest the inheritance.
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
            return -1;
        }
        return result.getVariables().getVarInteger(variable);
        //return getUserPermissionInteger(string, string1);
    }

    /**
     * Returns the variable value of the user, in INFO node.
     * If not found, it will search for his Group variables.
     * It will harvest the inheritance.
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
            return false;
        }
        return result.getVariables().getVarBoolean(variable);
        //return getUserPermissionBoolean(user, string1);
    }

    /**
     * Returns the variable value of the user, in INFO node.
     * If not found, it will search for his Group variables.
     * It will harvest the inheritance.
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
            return -1.0D;
        }
        return result.getVariables().getVarDouble(variable);
        //return getUserPermissionDouble(string, string1);
    }

    /**
     * Does not include User's group permission
     * @param user
     * @param permission
     * @return
     */
    public PermissionCheckResult checkUserOnlyPermission(User user, String permission) {
        user.sortPermissions();
        PermissionCheckResult result = new PermissionCheckResult();
        result.askedPermission = permission;
        result.owner = user;
        for (String access : user.getPermissionList()) {
            if (comparePermissionString(access, permission)) {
                result.accessLevel = access;
                if (access.startsWith("-")) {
                    result.resultType = PermissionCheckResult.Type.NEGATION;
                } else if (access.startsWith("+")) {
                    result.resultType = PermissionCheckResult.Type.EXCEPTION;
                } else {
                    result.resultType = PermissionCheckResult.Type.FOUND;
                }
                return result;
            }
        }
        result.resultType = PermissionCheckResult.Type.NOTFOUND;
        return result;
    }

    /**
     * Returns the node responsible for that permission.
     * Does not include User's group permission.
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
            if (comparePermissionString(access, permission)) {
                result.accessLevel = access;
                if (access.startsWith("-")) {
                    result.resultType = PermissionCheckResult.Type.NEGATION;
                } else if (access.startsWith("+")) {
                    result.resultType = PermissionCheckResult.Type.EXCEPTION;
                } else {
                    result.resultType = PermissionCheckResult.Type.FOUND;
                }
                return result;
            }
        }
        result.resultType = PermissionCheckResult.Type.NOTFOUND;
        return result;
    }

    /**
     * Check permissions, including it's group and inheritance.
     * @param user
     * @param permission
     * @return true if permission was found. false if not, or was negated.
     */
    public boolean checkUserPermission(User user, String permission) {
        PermissionCheckResult result = checkFullUserPermission(user, permission);
        if (result.resultType.equals(PermissionCheckResult.Type.EXCEPTION)
                || result.resultType.equals(PermissionCheckResult.Type.FOUND)) {
            return true;
        }
        return false;
    }

    /**
     * Do what checkUserPermission did before. But now returning a PermissionCheckResult.
     * @param user
     * @param targetPermission
     * @return
     */
    public PermissionCheckResult checkFullUserPermission(User user, String targetPermission) {
        PermissionCheckResult result = new PermissionCheckResult();
        result.askedPermission = targetPermission;
        result.resultType = PermissionCheckResult.Type.NOTFOUND;

        if (user == null || targetPermission == null) {
            return result;
        }

        PermissionCheckResult resultUser = checkUserOnlyPermission(user, targetPermission);
        if (!resultUser.resultType.equals(PermissionCheckResult.Type.NOTFOUND)) {
            return resultUser;

        }

        //IT ONLY CHECKS GROUPS PERMISSIONS IF RESULT FOR USER IS NOT FOUND
        PermissionCheckResult resultGroup = checkGroupPermissionWithInheritance(user.getGroup(), targetPermission);
        if (!resultGroup.resultType.equals(PermissionCheckResult.Type.NOTFOUND)) {
            return resultGroup;
        }

        //SUBGROUPS CHECK
        for (Group subGroup : user.subGroupListCopy()) {
            PermissionCheckResult resultSubGroup = checkGroupPermissionWithInheritance(subGroup, targetPermission);
            if (!resultSubGroup.resultType.equals(PermissionCheckResult.Type.NOTFOUND)) {
                return resultSubGroup;
            }
        }

        //THEN IT RETURNS A NOT FOUND
        return result;
    }

    /**
     * Verifies if a given group has a variable. Including it's inheritance.
     *
     * it redirects to the other method now. This one was deprecated, and will
     * be gone in a future release.
     *
     * @param start
     * @param variable
     * @param alreadyChecked
     * @return returns the closest inherited group with the variable.
     * @deprecated use now nextGroupWithVariable(Group start, String targetVariable)
     */
    @Deprecated
    public Group nextGroupWithVariable(Group start, String variable, List<Group> alreadyChecked) {
        return nextGroupWithVariable(start, variable);
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
     * redirected to the other method. this is deprecated now. and will be gone
     * in the future releases.
     *
     * @param start The group to start the search.
     * @param askedGroup Name of the group you're looking for
     * @param alreadyChecked groups to ignore(pass null on it, please)
     * @return true if it inherits the group.
     * @deprecated prefer using hasGroupInInheritance(Group start, String askedGroup)
     */
    @Deprecated
    public boolean searchGroupInInheritance(Group start, String askedGroup, List<Group> alreadyChecked) {
        return hasGroupInInheritance(start, askedGroup);
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
     * Check if the group has given permission. Including it's inheritance
     * @param start
     * @param permission
     * @param alreadyChecked
     * @return true if PermissionCheckResult is EXCEPTION or FOUND
     * @deprecated use the other checkGroupPermissionWithInheritance for everything
     */
    @Deprecated
    public boolean checkGroupPermissionWithInheritance(Group start, String permission, List<Group> alreadyChecked) {
        PermissionCheckResult result = checkGroupPermissionWithInheritance(start, permission);
        if (result.resultType.equals(result.resultType.EXCEPTION)
                || result.resultType.equals(result.resultType.FOUND)) {
            return true;
        }
        return false;
    }

    /**
     * Returns the result of permission check. Including inheritance.
     * If found anything, the PermissionCheckResult that retuns will
     * include the Group name, and the result type.
     * Result types will be EXCEPTION, NEGATION, FOUND.
     *
     * If returned type NOTFOUND, the owner will be null,
     * and ownerType too.
     *
     * It does Breadth-first search
     *
     * @param start
     * @param targetPermission
     * @return
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
                return resultNow;
            }
            for (String sonName : now.getInherits()) {
                Group son = ph.getGroup(sonName);
                if (son != null && !alreadyVisited.contains(son)) {
                    stack.push(son);
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
     * It uses checkGroupPermissionWithInheritance
     * and cast the owner to Group type if result type was EXCEPTION or FOUND.
     *
     * @param start
     * @param permission
     * @param alreadyChecked
     * @return the group that passed on test. null if no group passed.
     * @deprecated use checkGroupPermissionWithInheritance for everything now.
     */
    @Deprecated
    public Group nextGroupWithPermission(Group start, String permission, List<Group> alreadyChecked) {
        PermissionCheckResult result = checkGroupPermissionWithInheritance(start, permission);
        if (result.resultType.equals(result.resultType.EXCEPTION)
                || result.resultType.equals(result.resultType.FOUND)) {
            return (Group) checkGroupPermissionWithInheritance(start, permission).owner;
        }
        return null;
    }

    /**
     * Return whole list of names of groups in a inheritance chain. Including a
     * starting group.
     *
     * it now redirects to the other method. but get away from this one,
     * it will disappear in a future release.
     *
     * @param start
     * @param alreadyChecked
     * @return the group that passed on test. null if no group passed.
     * @deprecated  use the other method with same name, instead
     */
    @Deprecated
    public ArrayList<String> listAllGroupsInherited(Group start, ArrayList<String> alreadyChecked) {
        return listAllGroupsInherited(start);
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
     * permission name, like 'myplugin.dosomething'.
     * As the example above, will return true.
     *
     * Please sort permissions before sending them here. So negative tokens
     * get priority.
     *
     * You must test if it start with negative outside this method. It will
     * only tell if the nodes are matching or not.
     *
     * Every '-' or '+' in the beginning is ignored. It will match only
     * node names.
     *
     * @param userAcessLevel
     * @param fullPermissionName
     * @return true if found a matching token. false if not.
     */
    public boolean comparePermissionString(String userAcessLevel, String fullPermissionName) {
        if (userAcessLevel == null || fullPermissionName == null) {
            return false;
        }
        GroupManager.logger.finest("COMPARING " + userAcessLevel + " WITH " + fullPermissionName);

        if (userAcessLevel.startsWith("+")) {
            userAcessLevel = userAcessLevel.substring(1);
        } else if (userAcessLevel.startsWith("-")) {
            userAcessLevel = userAcessLevel.substring(1);
        }

        if (fullPermissionName.startsWith("+")) {
            fullPermissionName = fullPermissionName.substring(1);
        } else if (fullPermissionName.startsWith("-")) {
            fullPermissionName = fullPermissionName.substring(1);
        }


        StringTokenizer levelATokenizer = new StringTokenizer(userAcessLevel, ".");
        StringTokenizer levelBTokenizer = new StringTokenizer(fullPermissionName, ".");
        while (levelATokenizer.hasMoreTokens() && levelBTokenizer.hasMoreTokens()) {
            String levelA = levelATokenizer.nextToken();
            String levelB = levelBTokenizer.nextToken();
            GroupManager.logger.finest("ROUND " + levelA + " AGAINST " + levelB);
            if (levelA.contains("*")) {
                GroupManager.logger.finest("WIN");
                return true;
            }
            if (levelA.equalsIgnoreCase(levelB)) {
                if (!levelATokenizer.hasMoreTokens() && !levelBTokenizer.hasMoreTokens()) {
                    GroupManager.logger.finest("WIN");
                    return true;
                }
                GroupManager.logger.finest("NEXT");
                continue;
            } else {
                GroupManager.logger.finest("FAIL");
                return false;
            }

        }
        GroupManager.logger.finest("FAIL");
        return false;
    }

    /**
     * Returns a list of all groups.
     *
     * Including subgroups.
     * @param userName
     * @return
     */
    public String[] getGroups(String userName) {
        ArrayList<String> allGroups = listAllGroupsInherited(ph.getUser(userName).getGroup());
        for(Group subg: ph.getUser(userName).subGroupListCopy()){
            allGroups.addAll(listAllGroupsInherited(subg));
        }
        String[] arr = new String[allGroups.size()];
        return allGroups.toArray(arr);
    }

    /**
     * A Breadth-first search thru inheritance model.
     *
     * Just a model to copy and paste.
     * This will guarantee the closer groups will be checked first.
     * @param start
     * @param targerPermission
     * @return
     */
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
            if (resultNow.resultType.equals(PermissionCheckResult.Type.EXCEPTION)
                    || resultNow.resultType.equals(PermissionCheckResult.Type.FOUND)) {
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
}
