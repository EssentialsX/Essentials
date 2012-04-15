/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * 
 * @author gabrielcouto
 */
public class GMLoggerHandler extends ConsoleHandler {

	@Override
	public void publish(LogRecord record) {

		String message = "GroupManager - " + record.getLevel() + " - " + record.getMessage();
		if (record.getLevel().equals(Level.SEVERE) || record.getLevel().equals(Level.WARNING)) {
			System.err.println(message);
		} else {
			System.out.println(message);
		}
	}
}
