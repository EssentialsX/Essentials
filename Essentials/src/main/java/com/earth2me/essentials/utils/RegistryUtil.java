package com.earth2me.essentials.utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public final class RegistryUtil {
    private static final Table<Class<?>, String, Object> registryCache = HashBasedTable.create();

    private RegistryUtil() {
    }

    public static <T> T valueOf(Class<T> registry, String... names) {
        for (final String name : names) {
            //noinspection unchecked
            T value = (T) registryCache.get(registry, name);
            if (value != null) {
                return value;
            }

            try {
                //noinspection unchecked
                value = (T) registry.getDeclaredField(name).get(null);
                if (value != null) {
                    registryCache.put(registry, name, value);
                    return value;
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
        return null;
    }
}
