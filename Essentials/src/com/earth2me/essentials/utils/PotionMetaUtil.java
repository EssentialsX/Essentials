package com.earth2me.essentials.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class PotionMetaUtil {
    @SuppressWarnings("deprecation")
    public static ItemStack createPotionItem(int effectId) throws IllegalArgumentException {
        int damageValue = getBit(effectId, 0) +
                2 * getBit(effectId, 1) +
                4 * getBit(effectId, 2) +
                8 * getBit(effectId, 3);

        PotionType type = PotionType.getByDamageValue(damageValue);
        if (getBit(effectId, 15) != 1 || type == null) {
            throw new IllegalArgumentException("Unable to process potion effect ID " + effectId);
        }

        int level = getBit(effectId, 5) + 1;
        boolean extended = getBit(effectId, 6) == 1;
        boolean splash = getBit(effectId, 14) == 1;

        Potion potion = new Potion(type, level);
        potion.setHasExtendedDuration(extended);
        potion.setSplash(splash);

        return potion.toItemStack(1);
    }

    private static int getBit(int n, int k) {
        return (n >> k) & 1;
    }
}
