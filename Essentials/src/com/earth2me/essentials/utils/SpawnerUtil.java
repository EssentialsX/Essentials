package com.earth2me.essentials.utils;

import com.google.common.collect.ImmutableMap;
import net.ess3.api.IEssentials;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class SpawnerUtil {
    private boolean useMeta;
    private Map<EntityType, String> entityToDisplayName = ImmutableMap.<EntityType, String>builder()
            .put(EntityType.CAVE_SPIDER, "Cave Spider")
            .put(EntityType.PIG_ZOMBIE, "Zombie Pigman")
            .put(EntityType.MAGMA_CUBE, "Magma Cube")
            .put(EntityType.ENDER_DRAGON, "Ender Dragon")
            .put(EntityType.MUSHROOM_COW, "Mooshroom")
            .put(EntityType.SNOWMAN, "Snow Golem")
            .put(EntityType.OCELOT, "Ocelot")
            .put(EntityType.IRON_GOLEM, "Iron Golem")
            .put(EntityType.WITHER, "Wither")
            .put(EntityType.HORSE, "Horse")
            .build();

    public SpawnerUtil(IEssentials ess) {
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

    public ItemStack setEntityType(ItemStack is, EntityType type) throws IllegalArgumentException {
        if (useMeta) {
            // Supported in 1.8.3-R0.1-SNAPSHOT and above
            BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
            BlockState bs = bsm.getBlockState();
            ((CreatureSpawner) bs).setSpawnedType(type);
            bsm.setBlockState(bs);
            is.setItemMeta(bsm);
        } else {
            // Legacy behavior
            is.setDurability(type.getTypeId());
        }
        ItemMeta meta = is.getItemMeta();
        String displayName;
        if (entityToDisplayName.containsKey(type)) {
            displayName = entityToDisplayName.get(type);
        } else {
            displayName = type.getName();
        }
        meta.setDisplayName(ChatColor.RESET + displayName + " Spawner");
        is.setItemMeta(meta);
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
