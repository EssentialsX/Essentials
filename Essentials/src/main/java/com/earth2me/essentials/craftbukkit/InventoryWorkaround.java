package com.earth2me.essentials.craftbukkit;

import com.earth2me.essentials.utils.VersionUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/*
 * This class can be removed when https://github.com/Bukkit/CraftBukkit/pull/193 is accepted to CraftBukkit
 */
public final class InventoryWorkaround {
    private static final boolean IS_OFFHAND = VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_9_R01);

    private InventoryWorkaround() {
    }

    @SuppressWarnings("deprecation")
    public static void setItemInMainHand(final Player p, final ItemStack item) {
        if (IS_OFFHAND) {
            p.getInventory().setItemInMainHand(item);
        } else {
            p.setItemInHand(item);
        }
    }

    @SuppressWarnings("deprecation")
    public static void setItemInMainHand(final EntityEquipment invent, final ItemStack item) {
        if (IS_OFFHAND) {
            invent.setItemInMainHand(item);
        } else {
            invent.setItemInHand(item);
        }
    }

    @SuppressWarnings("deprecation")
    public static void setItemInMainHandDropChance(final EntityEquipment invent, final float chance) {
        if (IS_OFFHAND) {
            invent.setItemInMainHandDropChance(chance);
        } else {
            invent.setItemInHandDropChance(chance);
        }
    }
}
