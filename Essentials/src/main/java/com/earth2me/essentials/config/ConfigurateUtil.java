package com.earth2me.essentials.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ConfigurateUtil {
    private ConfigurateUtil() {
    }

    public static Set<String> getRootNodeKeys(final EssentialsConfiguration config) {
        return getKeys(config.getRootNode());
    }

    public static Set<String> getKeys(final CommentedConfigurationNode node) {
        if (node == null || !node.isMap()) {
            return Collections.emptySet();
        }

        final Set<String> keys = new LinkedHashSet<>();
        for (Object obj : node.childrenMap().keySet()) {
            keys.add(String.valueOf(obj));
        }
        return keys;
    }

    public static Map<String, CommentedConfigurationNode> getMap(final CommentedConfigurationNode node) {
        if (node == null || !node.isMap()) {
            return Collections.emptyMap();
        }

        final Map<String, CommentedConfigurationNode> map = new LinkedHashMap<>();
        for (Map.Entry<Object, CommentedConfigurationNode> entry : node.childrenMap().entrySet()) {
            map.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return map;
    }

    public static Map<String, Object> getRawMap(final EssentialsConfiguration config, final String key) {
        if (config == null || key == null) {
            return Collections.emptyMap();
        }
        return getRawMap(config.getSection(key));
    }

    public static Map<String, Object> getRawMap(final CommentedConfigurationNode node) {
        if (node == null || !node.isMap()) {
            return Collections.emptyMap();
        }

        final Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<Object, CommentedConfigurationNode> entry : node.childrenMap().entrySet()) {
            map.put(String.valueOf(entry.getKey()), entry.getValue().raw());
        }
        return map;
    }

    public static List<Map<?, ?>> getMapList(final CommentedConfigurationNode node) {
        List<?> list = null;
        try {
            list = node.getList(Object.class);
        } catch (SerializationException ignored) {
        }
        final List<Map<?, ?>> result = new ArrayList<>();

        if (list == null) {
            return result;
        }

        for (Object object : list) {
            if (object instanceof Map) {
                result.add((Map<?, ?>) object);
            }
        }
        return result;
    }

    public static BigDecimal toBigDecimal(final String input, final BigDecimal def) {
        if (input == null || input.isEmpty()) {
            return def;
        }

        try {
            return new BigDecimal(input, MathContext.DECIMAL128);
        } catch (final NumberFormatException | ArithmeticException e) {
            return def;
        }
    }

    public static boolean isDouble(final CommentedConfigurationNode node) {
        return node != null && node.raw() instanceof Double;
    }

    public static boolean isInt(final CommentedConfigurationNode node) {
        return node != null && node.raw() instanceof Integer;
    }

    public static boolean isString(final CommentedConfigurationNode node) {
        return node != null && node.raw() instanceof String;
    }
}
