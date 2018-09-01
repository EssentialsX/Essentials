package com.earth2me.essentials.utils;

import org.bukkit.Material;
import org.bukkit.Statistic;

import java.lang.reflect.Field;

public class EnumUtil {

    /**
     * Looks up enum fields by checking multiple names.
     */
    public static <T extends Enum> T valueOf(Class<T> enumClass, String... names) {
        for (String name : names) {
            try {
                Field enumField = enumClass.getDeclaredField(name);

                if (enumField.isEnumConstant()) {
                    return (T) enumField.get(null);
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }

        return null;
    }

    public static Material getMaterial(String... names) {
        return valueOf(Material.class, names);
    }

    public static Statistic getStatistic(String... names) {
        return valueOf(Statistic.class, names);
    }

}
