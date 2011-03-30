/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.data;

import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author gabrielcouto
 */
public class Group extends DataUnit implements Cloneable {

    /**
     * The group it inherits DIRECTLY!
     */
    private ArrayList<String> inherits = new ArrayList<String>();
    /**
     *This one holds the fields in INFO node.
     * like prefix = 'c'
     * or build = false
     */
    private GroupVariables variables = new GroupVariables(this);

    /**
     *
     * @param name
     */
    public Group(WorldDataHolder source, String name) {
        super(source,name);
    }

    /**
     *  Clone this group
     * @return a clone of this group
     */
    @Override
    public Group clone() {
        Group clone = new Group(getDataSource(), this.getName());
        clone.inherits = ((ArrayList<String>) this.getInherits().clone());
        for(String perm: this.getPermissionList()){
            clone.addPermission(perm);
        }
        clone.variables = ((GroupVariables) variables).clone(clone);
        //clone.flagAsChanged();
        return clone;
    }

    /**
     * Use this to deliver a group from a different dataSource to another
     * @param dataSource
     * @return
     */
    public Group clone(WorldDataHolder dataSource) {
        if (dataSource.groupExists(this.getName())) {
            return null;
        }
        Group clone = getDataSource().createGroup(this.getName());
        clone.inherits = ((ArrayList<String>) this.getInherits().clone());
        for(String perm: this.getPermissionList()){
            clone.addPermission(perm);
        }
        clone.variables = variables.clone(clone);
        clone.flagAsChanged(); //use this to make the new dataSource save the new group
        return clone;
    }

    /**
     * a COPY of inherits list
     * You can't manage the list by here
     * Lol... version 0.6 had a problem because this.
     * @return the inherits
     */
    public ArrayList<String> getInherits() {
        return (ArrayList<String>) inherits.clone();
    }

    /**
     * @param inherits the inherits to set
     */
    public void addInherits(Group inherit) {
        if (!this.getDataSource().groupExists(inherit.getName())) {
            getDataSource().addGroup(inherit);
        }
        if (!inherits.contains(inherit.getName().toLowerCase())) {
            inherits.add(inherit.getName().toLowerCase());
        }
        flagAsChanged();
    }

    public boolean removeInherits(String inherit) {
        if (this.inherits.contains(inherit.toLowerCase())) {
            this.inherits.remove(inherit.toLowerCase());
            flagAsChanged();
            return true;
        }
        return false;
    }

    /**
     * @return the variables
     */
    public GroupVariables getVariables() {
        return variables;
    }

    /**
     * 
     * @param varList
     */
    public void setVariables(Map<String, Object> varList) {
        GroupVariables temp = new GroupVariables(this, varList);
        variables.clearVars();
        for(String key: temp.getVarKeyList()){
            variables.addVar(key, temp.getVarObject(key));
        }
        flagAsChanged();
    }
}
