package com.earth2me.essentials.utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class RegistryUtil {
    private static final Table<Class<?>, String, Object> registryCache = HashBasedTable.create();

    private RegistryUtil() {
    }

    public static <T> Object[] values(Class<T> registry) {
        if (registry.getEnumConstants() != null) {
            return registry.getEnumConstants();
        }

        //noinspection unchecked
        final T[] values = (T[]) registryCache.get(registry, "$values");
        if (values != null) {
            return values;
        }

        final List<T> set = new ArrayList<>();

        for (final Field field : registry.getDeclaredFields()) {
            try {
                final Object value = field.get(null);
                if (value != null && registry.isAssignableFrom(value.getClass())) {
                    //noinspection unchecked
                    set.add((T) value);
                }
            } catch (NullPointerException | IllegalAccessException ignored) {
            }
        }

        //noinspection unchecked
        final T[] array = (T[]) new Object[set.size()];
        for (int i = 0; i < set.size(); i++) {
            array[i] = set.get(i);
        }
        registryCache.put(registry, "$values", array);

        return array;
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
