/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.data;

import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.util.ArrayList;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import java.util.Map;

/**
 *
 * @author gabrielcouto
 */
public class User extends DataUnit implements Cloneable {

    /**
     *
     */
    private String group = null;
    private ArrayList<String> subGroups = new ArrayList<String>();
    /**
     *This one holds the fields in INFO node.
     * like prefix = 'c'
     * or build = false
     */
    private UserVariables variables = new UserVariables(this);


    /**
     *
     * @param name
     */
    public User(WorldDataHolder source, String name) {
        super(source,name);
        this.group = source.getDefaultGroup().getName();
    }

    /**
     *
     * @return
     */
    @Override
    public User clone() {
        User clone = new User(getDataSource(), this.getName());
        clone.group = this.group;
        for(String perm: this.getPermissionList()){
            clone.addPermission(perm);
        }
        //clone.variables = this.variables.clone();
        //clone.flagAsChanged();
        return clone;
    }

    /**
     * Use this to deliver a user from one WorldDataHolder to another
     * @param dataSource
     * @return null if given dataSource already contains the same user
     */
    public User clone(WorldDataHolder dataSource) {
        if (dataSource.isUserDeclared(this.getName())) {
            return null;
        }
        User clone = dataSource.createUser(this.getName());
        if (dataSource.getGroup(group) == null) {
            clone.setGroup(dataSource.getDefaultGroup());
        } else {
            clone.setGroup(this.getGroupName());
        }
        for(String perm: this.getPermissionList()){
            clone.addPermission(perm);
        }
        //clone.variables = this.variables.clone();
        clone.flagAsChanged();
        return clone;
    }

    public Group getGroup() {
        Group result = getDataSource().getGroup(group);
        if (result == null) {
            this.setGroup(getDataSource().getDefaultGroup());
            result = getDataSource().getDefaultGroup();
        }
        return result;
    }

    /**
     * @return the group
     */
    public String getGroupName() {
        Group result = getDataSource().getGroup(group);
        if (result == null) {
            group = getDataSource().getDefaultGroup().getName();
        }
        return group;
    }

    /**
     * @param group the group to set
     */
    @Deprecated
    public void setGroup(String group) {
        this.group = group;
        flagAsChanged();
    }

    /**
     * @param group the group to set
     */
    public void setGroup(Group group) {
        if (!this.getDataSource().groupExists(group.getName())) {
            getDataSource().addGroup(group);
        }
        group = getDataSource().getGroup(group.getName());
        this.group = group.getName();
        flagAsChanged();
    }

    public void addSubGroup(Group subGroup){
        if(this.group.equalsIgnoreCase(subGroup.getName())){
            return;
        }
        if (!this.getDataSource().groupExists(subGroup.getName())) {
            getDataSource().addGroup(subGroup);
        }  
        subGroup = getDataSource().getGroup(subGroup.getName());
        removeSubGroup(subGroup);
        subGroups.add(subGroup.getName());
        flagAsChanged();
    }
    public int subGroupsSize(){
        return subGroups.size();
    }
    public boolean isSubGroupsEmpty(){
        return subGroups.isEmpty();
    }
    public boolean containsSubGroup(Group subGroup){
        return subGroups.contains(subGroup.getName());
    }
    public boolean removeSubGroup(Group subGroup){
        try{
            if(subGroups.remove(subGroup.getName())){
                flagAsChanged();
                return true;
            }
        } catch (Exception e){

        }
        return false;
    }
    public ArrayList<Group> subGroupListCopy(){
        ArrayList<Group> val = new ArrayList<Group>();
        for(String gstr: subGroups){
            Group g = getDataSource().getGroup(gstr);
            if(g==null){
                removeSubGroup(g);
                continue;
            }
            val.add(g);
        }
        return val;
    }
    public ArrayList<String> subGroupListStringCopy(){
        return (ArrayList<String>) subGroups.clone();
    }

    /**
     * @return the variables
     */
    public UserVariables getVariables() {
        return variables;
    }

    /**
     *
     * @param varList
     */
    public void setVariables(Map<String, Object> varList) {
        UserVariables temp = new UserVariables(this, varList);
        variables.clearVars();
        for(String key: temp.getVarKeyList()){
            variables.addVar(key, temp.getVarObject(key));
        }
        flagAsChanged();
    }
}
