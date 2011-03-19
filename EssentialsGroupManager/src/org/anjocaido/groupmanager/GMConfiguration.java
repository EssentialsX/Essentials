/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private GroupManager plugin;
    private Map<String, Object> rootDataNode;
    private File configFile;

    public GMConfiguration(GroupManager plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                Tasks.copy(plugin.getResourceAsStream("config.yml"), configFile);
            } catch (IOException ex) {
                GroupManager.logger.log(Level.SEVERE, null, ex);
            }
        }

        Yaml yaml = new Yaml(new SafeConstructor());
        FileInputStream rx = null;
        try {
            rx = new FileInputStream(configFile);
        } catch (FileNotFoundException ex) {
            GroupManager.logger.log(Level.SEVERE, null, ex);
        }
        try {
            rootDataNode = (Map<String, Object>) yaml.load(new UnicodeReader(rx));
            if (rootDataNode == null) {
                throw new NullPointerException();
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("The following file couldn't pass on Parser.\n" + configFile.getPath(), ex);
        } finally {
            try {
                rx.close();
            } catch (IOException ex) {
            }
        }
        adjustLoggerLevel();
    }

    public Map<String, Object> getMirrorsMap() {
        if (rootDataNode.get("settings") instanceof Map) {
            Map<String, Object> settingsNode = (Map<String, Object>) rootDataNode.get("settings");
            if (settingsNode.get("permission") instanceof Map) {
                Map<String, Object> permissionNode = (Map<String, Object>) settingsNode.get("permission");
                if (permissionNode.get("world") instanceof Map) {
                    Map<String, Object> worldsNode = (Map<String, Object>) permissionNode.get("world");
                    if (worldsNode.get("mirror") instanceof Map) {
                        Map<String, Object> mirrorsNode = (Map<String, Object>) worldsNode.get("mirror");
                        return mirrorsNode;
                    }
                }
            }
        }
        return null;
    }

    public Integer getSaveInterval() {
        if (rootDataNode.get("settings") instanceof Map) {
            Map<String, Object> settingsNode = (Map<String, Object>) rootDataNode.get("settings");
            if (settingsNode.get("data") instanceof Map) {
                Map<String, Object> dataNode = (Map<String, Object>) settingsNode.get("data");
                if (dataNode.get("save") instanceof Map) {
                    Map<String, Object> saveNode = (Map<String, Object>) dataNode.get("save");
                    if (saveNode.get("minutes") instanceof Integer) {
                        return (Integer) saveNode.get("minutes");
                    }
                }
            }
        }
        return 10;
    }

    public void adjustLoggerLevel() {
        if (rootDataNode.get("settings") instanceof Map) {
            Map<String, Object> settingsNode = (Map<String, Object>) rootDataNode.get("settings");
            if (settingsNode.get("logging") instanceof Map) {
                Map<String, Object> loggingNode = (Map<String, Object>) settingsNode.get("logging");
                if (loggingNode.get("level") instanceof String) {
                    String level = (String) loggingNode.get("level");
                    try {
                        GroupManager.logger.setLevel(Level.parse(level));
                        return;
                    } catch (Exception e) {
                    }
                }
            }
        }
        GroupManager.logger.setLevel(Level.INFO);
    }
}
