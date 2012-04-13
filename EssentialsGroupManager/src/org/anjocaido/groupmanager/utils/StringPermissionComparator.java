/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.utils;

import java.util.Comparator;

/**
 * 
 * @author gabrielcouto
 */
public class StringPermissionComparator implements Comparator<String> {

	@Override
	public int compare(String permA, String permB) {

		boolean ap = permA.startsWith("+");
		boolean bp = permB.startsWith("+");
		boolean am = permA.startsWith("-");
		boolean bm = permB.startsWith("-");
		if (ap && bp) {
			return 0;
		}
		if (ap && !bp) {
			return -1;
		}
		if (!ap && bp) {
			return 1;
		}
		if (am && bm) {
			return 0;
		}
		if (am && !bm) {
			return -1;
		}
		if (!am && bm) {
			return 1;
		}
		return permA.compareToIgnoreCase(permB);
	}

	private static StringPermissionComparator instance;

	public static StringPermissionComparator getInstance() {

		if (instance == null) {
			instance = new StringPermissionComparator();
		}
		return instance;
	}
}
