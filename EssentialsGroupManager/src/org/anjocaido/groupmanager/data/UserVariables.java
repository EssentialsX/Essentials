/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.data;

import java.util.Map;

/**
 * 
 * @author gabrielcouto
 */
public class UserVariables extends Variables {

	private User owner;

	public UserVariables(User owner) {

		super(owner);
		this.owner = owner;
	}

	public UserVariables(User owner, Map<String, Object> varList) {

		super(owner);
		this.variables.clear();
		this.variables.putAll(varList);
		this.owner = owner;
	}

	/**
	 * A clone of all vars here.
	 * 
	 * @return UserVariables clone
	 */
	protected UserVariables clone(User newOwner) {

		UserVariables clone = new UserVariables(newOwner);
		synchronized(variables) {
		for (String key : variables.keySet()) {
			clone.variables.put(key, variables.get(key));
		}
		}
		newOwner.flagAsChanged();
		return clone;
	}

	/**
	 * @return the owner
	 */
	@Override
	public User getOwner() {

		return owner;
	}
}
