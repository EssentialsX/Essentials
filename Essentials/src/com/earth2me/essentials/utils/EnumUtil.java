package com.earth2me.essentials.utils;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Set;

public final class EnumUtil {

    private EnumUtil() {
    }

    /**
     * Returns the field matching the first provided enum name that exists within the given
     * enum class. If no field is found, this method returns null.
     *
     * @param enumClass The class to search through
     * @param names     The names of the fields to search for
     * @param <T>       The enum to search through
     * @return The first matching enum field
     */
    public static <T extends Enum<T>> T valueOf(final Class<T> enumClass, final String... names) {
        for (final String name : names) {
            try {
                final Field enumField = enumClass.getDeclaredField(name);

                if (enumField.isEnumConstant()) {
                    return (T) enumField.get(null);
                }
            } catch (final NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        return null;
    }

    /**
     * Return a set containing <b>all</b> fields of the given enum that match one of the provided
     * names.
     *
     * @param enumClass The class to search through
     * @param names     The names of the fields to search for
     * @param <T>       The enum to search through
     * @return All matching enum fields
     */
    public static <T extends Enum<T>> Set<T> getAllMatching(final Class<T> enumClass, final String... names) {
        final Set<T> set = EnumSet.noneOf(enumClass);

        for (final String name : names) {
            try {
                final Field enumField = enumClass.getDeclaredField(name);

                if (enumField.isEnumConstant()) {
                    set.add((T) enumField.get(null));
                }
            } catch (final NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        return set;
    }

    /**
     * Gets the first Material field found from the given names.
     *
     * @param names The names of the fields to search for
     * @return The first matching Material
     */
    public static Material getMaterial(final String... names) {
        return valueOf(Material.class, names);
    }

    /**
     * Gets the first Statistic field found from the given names.
     *
     * @param names The names of the fields to search for
     * @return The first matching Statistic
     */
    public static Statistic getStatistic(final String... names) {
        return valueOf(Statistic.class, names);
    }

    public static EntityType getEntityType(final String... names) {
        return valueOf(EntityType.class, names);
    }
}
