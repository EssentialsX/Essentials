package net.ess3.provider;

import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public interface SpawnerProvider extends Provider {
    ItemStack setEntityType(ItemStack is, EntityType type) throws IllegalArgumentException;

    EntityType getEntityType(ItemStack is) throws IllegalArgumentException;

    Map<EntityType, String> entityToDisplayName = ImmutableMap.<EntityType, String>builder()
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

    default ItemStack setDisplayName(ItemStack is, EntityType type) {
        ItemMeta meta = is.getItemMeta();
        String displayName;
        if (entityToDisplayName.containsKey(type)) {
            displayName = entityToDisplayName.get(type);
        } else {
            //noinspection deprecation
            displayName = type.getName();
        }
        meta.setDisplayName(ChatColor.RESET + displayName + " Spawner");
        is.setItemMeta(meta);
        return is;
    }
}
