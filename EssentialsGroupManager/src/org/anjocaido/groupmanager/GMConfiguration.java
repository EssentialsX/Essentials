/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.anjocaido.groupmanager.utils.Tasks;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;

/**
 * 
 * @author gabrielcouto
 */
public class GMConfiguration {
	
	private boolean opOverride = true;
	private boolean toggleValidate = true;
	private Integer saveInterval = 10;
	private Integer backupDuration = 24;
	private String loggerLevel = "OFF";
	private Map<String, Object> mirrorsMap;
	

	private GroupManager plugin;
	private Map<String, Object> GMconfig;

	public GMConfiguration(GroupManager plugin) {

		this.plugin = plugin;
		
		/*
		 * Set defaults
		 */
		opOverride = true;
		toggleValidate = true;
		saveInterval = 10;
		backupDuration = 24;
		loggerLevel = "OFF";
				
		load();
	}

	@SuppressWarnings("unchecked")
	public void load() {

		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdirs();
		}

		File configFile = new File(plugin.getDataFolder(), "config.yml");

		if (!configFile.exists()) {
			try {
				Tasks.copy(plugin.getResourceAsStream("config.yml"), configFile);
			} catch (IOException ex) {
				GroupManager.logger.log(Level.SEVERE, "Error creating a new config.yml", ex);
			}
		}

		Yaml configYAML = new Yaml(new SafeConstructor());

		try {
			FileInputStream configInputStream = new FileInputStream(configFile);
			GMconfig = (Map<String, Object>) configYAML.load(new UnicodeReader(configInputStream));
			configInputStream.close();

		} catch (Exception ex) {
			throw new IllegalArgumentException("The following file couldn't pass on Parser.\n" + configFile.getPath(), ex);
		}

		/*
		 * Read our config settings ands store them for reading later.
		 */
		try {
			Map<String, Object> config = getElement("config", getElement("settings", GMconfig));

			opOverride = (Boolean) config.get("opOverrides");
			toggleValidate = (Boolean) config.get("validate_toggle");

			/*
			 * data node for save/backup timers.
			 */
			try {
				Map<String, Object> save = getElement("save", getElement("data", getElement("settings", GMconfig)));
				
				try {
					saveInterval = (Integer) save.get("minutes");
				} catch (Exception ex) {
					GroupManager.logger.log(Level.SEVERE, "Missing or corrupt 'minutes' node. Using default setting", ex);
				}
				
				try {
					backupDuration = (Integer) save.get("hours");
				} catch (Exception ex) {
					GroupManager.logger.log(Level.SEVERE, "Missing or corrupt 'hours' node. Using default setting", ex);
				}
				
			} catch (Exception ex) {
				GroupManager.logger.log(Level.SEVERE, "Missing or corrupt 'data' node. Using default settings", ex);
			}

			

			Object level = ((Map<String, String>) getElement("settings", GMconfig).get("logging")).get("level");
			if (level instanceof String)
				loggerLevel = (String) level;

			/*
			 * Store our mirrors map for parsing later.
			 */
			mirrorsMap = (Map<String, Object>) ((Map<String, Object>) GMconfig.get("settings")).get("mirrors");

		} catch (Exception ex) {
			/*
			 * Flag the error and use defaults
			 */
			GroupManager.logger.log(Level.SEVERE, "There are errors in your config.yml. Using default settings", ex);
			
			mirrorsMap = new HashMap<String, Object>();
		}
		// Setup defaults
		adjustLoggerLevel();
		plugin.setValidateOnlinePlayer(isToggleValidate());
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getElement(String element, Map<String, Object> map) {
		
		if (!map.containsKey(element)) {
			throw new IllegalArgumentException("The config.yml has no '" + element + ".\n");
		}
		
		return (Map<String, Object>) map.get(element);
		
	}

	public boolean isOpOverride() {

		return opOverride;
	}

	public boolean isToggleValidate() {
		
		return toggleValidate;
	}

	public Integer getSaveInterval() {

		return saveInterval;
	}

	public Integer getBackupDuration() {

		return backupDuration;
	}

	public void adjustLoggerLevel() {

		try {
			GroupManager.logger.setLevel(Level.parse(loggerLevel));
			return;
		} catch (Exception e) {
		}

		GroupManager.logger.setLevel(Level.INFO);
	}
	
	public Map<String, Object> getMirrorsMap() {

		if (!mirrorsMap.isEmpty()) {
			return mirrorsMap;
		}
		return null;

	}

}