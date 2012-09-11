/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that holds variables of a user/group.
 * In groups, it holds the contents of INFO node.
 * Like:
 * prefix
 * suffix
 * build
 * 
 * @author gabrielcouto
 */
public abstract class Variables implements Cloneable {

	private DataUnit owner;
	protected final Map<String, Object> variables = Collections.synchronizedMap(new HashMap<String, Object>());

	public Variables(DataUnit owner) {

		this.owner = owner;
	}

	/**
	 * Add var to the the INFO node.
	 * examples:
	 * addVar("build",true);
	 * addVar("prefix","c");
	 * 
	 * @param name key name of the var
	 * @param o the object value of the var
	 */
	public void addVar(String name, Object o) {

		if (o == null) {
			return;
		}
		if (variables.containsKey(name)) {
			variables.remove(name);
		}
		variables.put(name, o);
		owner.flagAsChanged();
	}

	/**
	 * Returns the object inside the var
	 * 
	 * @param name
	 * @return a Object if exists. null if doesn't exists
	 */
	public Object getVarObject(String name) {

		return variables.get(name);
	}

	/**
	 * Get the String value for the given var name
	 * 
	 * @param name the var key name
	 * @return "" if null. or the toString() value of object
	 */
	public String getVarString(String name) {

		Object o = variables.get(name);
		try {
			return o == null ? "" : o.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 
	 * @param name
	 * @return false if null. or a Boolean.parseBoolean of the string
	 */
	public Boolean getVarBoolean(String name) {

		Object o = variables.get(name);
		try {
			return o == null ? false : Boolean.parseBoolean(o.toString());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @param name
	 * @return -1 if null. or a parseInt of the string
	 */
	public Integer getVarInteger(String name) {

		Object o = variables.get(name);
		try {
			return o == null ? -1 : Integer.parseInt(o.toString());
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 
	 * @param name
	 * @return -1 if null. or a parseDouble of the string
	 */
	public Double getVarDouble(String name) {

		Object o = variables.get(name);
		try {
			return o == null ? -1.0D : Double.parseDouble(o.toString());
		} catch (Exception e) {
			return -1.0D;
		}
	}

	/**
	 * All variable keys this is holding
	 * 
	 * @return Set of all variable names.
	 */
	public String[] getVarKeyList() {
		synchronized(variables) {
			return variables.keySet().toArray(new String[0]);
		}
	}

	/**
	 * verify is a var exists
	 * 
	 * @param name the key name of the var
	 * @return true if that var exists
	 */
	public boolean hasVar(String name) {

		return variables.containsKey(name);
	}

	/**
	 * Returns the quantity of vars this is holding
	 * 
	 * @return the number of vars
	 */
	public int getSize() {

		return variables.size();
	}

	/**
	 * Remove a var from the list
	 * 
	 * @param name
	 */
	public void removeVar(String name) {

		try {
			variables.remove(name);
		} catch (Exception e) {
		}
		owner.flagAsChanged();
	}

	public static Object parseVariableValue(String value) {

		try {
			Integer i = Integer.parseInt(value);
			return i;
		} catch (NumberFormatException e) {
		}
		try {
			Double d = Double.parseDouble(value);
			return d;
		} catch (NumberFormatException e) {
		}
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("on")) {
			return true;
		} else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no") || value.equalsIgnoreCase("off")) {
			return false;
		}
		return value;

	}

	public void clearVars() {

		variables.clear();
		owner.flagAsChanged();
	}

	/**
	 * @return the owner
	 */
	public DataUnit getOwner() {

		return owner;
	}

	public boolean isEmpty() {

		return variables.isEmpty();
	}
}
