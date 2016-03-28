package com.earth2me.essentials.utils;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Map;

public class PotionMetaUtil {
    private static Map<Integer, PotionType> damageValueToType = ImmutableMap.<Integer, PotionType>builder()
            .put(1, PotionType.REGEN)
            .put(2, PotionType.SPEED)
            .put(3, PotionType.FIRE_RESISTANCE)
            .put(4, PotionType.POISON)
            .put(5, PotionType.INSTANT_HEAL)
            .put(6, PotionType.NIGHT_VISION)
            // Skip 7
            .put(8, PotionType.WEAKNESS)
            .put(9, PotionType.STRENGTH)
            .put(10, PotionType.SLOWNESS)
            .put(11, PotionType.JUMP)
            .put(12, PotionType.INSTANT_DAMAGE)
            .put(13, PotionType.WATER_BREATHING)
            .put(14, PotionType.INVISIBILITY)
            .build();

    public static ItemStack createPotionItem(int effectId) throws IllegalArgumentException {
        int damageValue = getBit(effectId, 0) +
                2 * getBit(effectId, 1) +
                4 * getBit(effectId, 2) +
                8 * getBit(effectId, 3);

        PotionType type = damageValueToType.get(damageValue);
        if (type == null) {
            throw new IllegalArgumentException("Unable to process potion effect ID " + effectId + " with damage value " + damageValue);
        }

        boolean extended = getBit(effectId, 6) == 1;
        boolean upgraded = getBit(effectId, 5) == 1;
        boolean splash = getBit(effectId, 14) == 1;

        ItemStack potion;
        if (splash) {
            potion = new ItemStack(Material.SPLASH_POTION, 1);
        } else {
            potion = new ItemStack(Material.POTION, 1);
        }

        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        PotionData potionType = new PotionData(type, extended, upgraded);

        return potion;
    }

    private static int getBit(int n, int k) {
        return (n >> k) & 1;
    }
}
