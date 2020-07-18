package com.earth2me.essentials.configuration;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Acrobot
 */
public class ValueParser {
    /**
     * Parses an object to a YAML-usable string
     *
     * @param object Object to parse
     * @return YAML string
     */
    public String parseToYAML(Object object) {
        if (object instanceof Number || object instanceof Boolean) {
            return String.valueOf(object);
        } else if (object instanceof Collection) {
            StringBuilder sb = new StringBuilder();
            //noinspection rawtypes
            for (Object o : (Collection) object) {
                sb.append("\n- ").append(parseToYAML(o));
            }
            return sb.toString();
        } else if (object instanceof String) {
            String[] lines = ((String) object).split("\\R");
            if (lines.length == 1) {
                return '\"' + String.valueOf(object) + '\"';
            } else {
                return "|-\n" + Arrays.stream(lines).map(s -> "  " + s).collect(Collectors.joining("\n"));
            }
        } else {
            return '\"' + String.valueOf(object) + '\"';
        }
    }

    /**
     * Parses a YAML "object" to Java-compatible object
     *
     * @param type The type of the returned object
     * @param object Object to parse
     * @return Java-compatible object
     */
    public <T> Object parseToJava(Class<T> type, Object object) {
        if (object instanceof ConfigurationSection) {
            Map<String, List<String>> map = new HashMap<>();

            for (String message : ((ConfigurationSection) object).getKeys(false)) {
                map.put(message, ((ConfigurationSection) object).getStringList(message));
            }

            return map;
        } else {
            return object;
        }
    }
}
