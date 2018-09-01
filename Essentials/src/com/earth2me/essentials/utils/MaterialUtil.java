package com.earth2me.essentials.utils;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

import static com.earth2me.essentials.utils.EnumUtil.getMaterial;

public class MaterialUtil {

    private static final Set<Material> BEDS = new HashSet<>();

    static {
        // Adds WHITE_BED if 1.13+, otherwise BED
        BEDS.add(getMaterial("WHITE_BED", "BED"));

        // Don't keep looking up and adding BED if we're not on 1.13+
        if (BEDS.add(getMaterial("ORANGE_BED", "BED"))) {
            BEDS.add(getMaterial("MAGENTA_BED", "BED"));
            BEDS.add(getMaterial("LIGHT_BLUE_BED", "BED"));
            BEDS.add(getMaterial("YELLOW_BED", "BED"));
            BEDS.add(getMaterial("LIME_BED", "BED"));
            BEDS.add(getMaterial("PINK_BED", "BED"));
            BEDS.add(getMaterial("GRAY_BED", "BED"));
            BEDS.add(getMaterial("LIGHT_GRAY_BED", "BED"));
            BEDS.add(getMaterial("CYAN_BED", "BED"));
            BEDS.add(getMaterial("PURPLE_BED", "BED"));
            BEDS.add(getMaterial("BLUE_BED", "BED"));
            BEDS.add(getMaterial("BROWN_BED", "BED"));
            BEDS.add(getMaterial("GREEN_BED", "BED"));
            BEDS.add(getMaterial("RED_BED", "BED"));
            BEDS.add(getMaterial("BLACK_BED", "BED"));
        }
    }

    public static boolean isBed(Material material) {
        return BEDS.contains(material);
    }

}
