package com.earth2me.essentials.utils;

import org.bukkit.Color;
import org.bukkit.DyeColor;

public class ColorUtil {
    public static DyeColor getDyeColor(String value) {
        try {
            return DyeColor.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return DyeColor.getByColor(Color.fromRGB(Integer.valueOf(value)));
        }
    }
}
