/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.data;

import java.util.ArrayList;
import java.util.Collections;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.anjocaido.groupmanager.utils.StringPermissionComparator;

/**
 *
 * @author gabrielcouto
 */
public abstract class DataUnit {

    private WorldDataHolder dataSource;
    private String name;
    private boolean changed;
    private ArrayList<String> permissions = new ArrayList<String>();

    public DataUnit(WorldDataHolder dataSource, String name) {
        this.dataSource = dataSource;
        this.name = name;
    }

    /**
     *  Every group is matched only by their names and DataSources names.
     * @param o
     * @return true if they are equal. false if not.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof DataUnit) {
            DataUnit go = (DataUnit) o;
            if (this.getName().equalsIgnoreCase(go.getName()) && this.dataSource.getName().equalsIgnoreCase(go.getDataSource().getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.name != null ? this.name.toLowerCase().hashCode() : 0);
        return hash;
    }


   

    /**
     * @return the dataSource
     */
    public WorldDataHolder getDataSource() {
        return dataSource;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public void flagAsChanged() {
        GroupManager.logger.finest("DataSource: "+getDataSource().getName()+" - DataUnit: "+getName()+" flagged as changed!");
//        for(StackTraceElement st: Thread.currentThread().getStackTrace()){
//            GroupManager.logger.finest(st.toString());
//        }
        changed = true;
    }

    public boolean isChanged() {
        return changed;
    }

    public void flagAsSaved() {
        GroupManager.logger.finest("DataSource: "+getDataSource().getName()+" - DataUnit: "+getName()+" flagged as saved!");
        changed = false;
    }

    public boolean hasSamePermissionNode(String permission) {
        return permissions.contains(permission);
    }

    public void addPermission(String permission) {
        if (!hasSamePermissionNode(permission)) {
            permissions.add(permission);
        }
        flagAsChanged();
    }

    public boolean removePermission(String permission) {
        flagAsChanged();
        return permissions.remove(permission);
    }

    /**
     * Use this only to list permissions.
     * You can't edit the permissions using the returned ArrayList instance
     * @return a copy of the permission list
     */
    public ArrayList<String> getPermissionList() {
        return (ArrayList<String>) permissions.clone();
    }

    public void sortPermissions(){
        Collections.sort(permissions, StringPermissionComparator.getInstance());
    }
}
