package com.earth2me.essentials.configuration;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class KeyValueParser extends ValueParser {

    private final Map<String, Object> defaultValue;

    public KeyValueParser() {
        this(new HashMap<>());
    }

    public KeyValueParser(Map<String, Object> defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public <T> Object parseToJava(Class<T> type, Object object) {
        if (object instanceof ConfigurationSection) {
            Map<String, Object> map = ((ConfigurationSection) object).getValues(false);
            if (!map.isEmpty()) {
                return map;
            }
        }
        return defaultValue;
    }

    @Override
    public String parseToYAML(Object object) {
        if (object instanceof Map) {
            //noinspection unchecked
            Map<Object, Object> map = (Map<Object, Object>) object;
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                sb.append("\n  \"").append(entry.getKey().toString()).append("\": ")
                        .append(Configuration.getParser(entry.getValue().getClass()).parseToYAML(entry.getValue()));
            }
            return sb.toString();
        }
        return "";
    }
}
