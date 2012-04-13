/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.utils;

import org.anjocaido.groupmanager.data.DataUnit;

/**
 * 
 * @author gabrielcouto
 */
public class PermissionCheckResult {

	/**
	 * It should be the owner of the access level found.
	 * 
	 * Use instanceof to find the owner type
	 */
	public DataUnit owner;
	/**
	 * The permission node found in the DataUnit.
	 */
	public String accessLevel;
	/**
	 * The full name of the permission you are looking for
	 */
	public String askedPermission;
	/**
	 * The result conclusion of the search.
	 * It determines if the owner can do, or not.
	 * 
	 * It even determines if it has an owner.
	 */
	public Type resultType = Type.NOTFOUND;

	/**
	 * The type of result the search can give.
	 */
	public enum Type {

		/**
		 * If found a matching node starting with '+'.
		 * It means the user CAN do the permission.
		 */
		EXCEPTION,
		/**
		 * If found a matching node starting with '-'.
		 * It means the user CANNOT do the permission.
		 */
		NEGATION,
		/**
		 * If just found a common matching node.
		 * IT means the user CAN do the permission.
		 */
		FOUND,
		/**
		 * If no matchin node was found.
		 * It means the user CANNOT do the permission.
		 * 
		 * owner field and accessLevel field should not be considered,
		 * when type is
		 * NOTFOUND
		 */
		NOTFOUND
	}
}
