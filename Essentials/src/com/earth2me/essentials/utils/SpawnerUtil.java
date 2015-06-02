package com.earth2me.essentials.utils;

import com.earth2me.essentials.Essentials;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnerUtil {
    private boolean useMeta;

    public SpawnerUtil(Essentials ess) {
        try {
            ItemStack is = new ItemStack(Material.MOB_SPAWNER, 1);
            ItemMeta meta = is.getItemMeta();
            useMeta = meta instanceof BlockStateMeta;
        } catch (Exception e) {
            useMeta = false;
        }
        if (useMeta) {
            ess.getLogger().info("Using BlockStateMeta for spawners");
        } else {
            ess.getLogger().info("Using legacy item data for spawners");
        }
    }

    public ItemStack setEntityType(EntityType type) {
        ItemStack is = new ItemStack(Material.MOB_SPAWNER, 1);
        if (useMeta) {
            // Supported in 1.8.3-R0.1-SNAPSHOT and above
            BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
            BlockState bs = bsm.getBlockState();
            ((CreatureSpawner) bs).setSpawnedType(type);
            bsm.setBlockState(bs);
            is.setItemMeta(bsm);
        } else {
            // Legacy behavior
            is.getData().setData((byte) type.ordinal());
        }
        return is;
    }

    public EntityType getEntityType(ItemStack is) {
        ItemMeta meta = is.getItemMeta();
        if (useMeta) {
            BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
            CreatureSpawner bs = (CreatureSpawner) bsm.getBlockState();
            return bs.getSpawnedType();
        } else {
            return EntityType.fromId((int) is.getData().getData());
        }
    }
}
