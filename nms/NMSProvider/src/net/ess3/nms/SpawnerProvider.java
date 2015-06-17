package net.ess3.nms;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class SpawnerProvider {
    protected Map<EntityType, String> entityToDisplayName = ImmutableMap.<EntityType, String>builder()
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

    public abstract ItemStack setEntityType(ItemStack is, EntityType type) throws IllegalArgumentException;
    public abstract EntityType getEntityType(ItemStack is) throws IllegalArgumentException;
    public abstract String getHumanName();

    public boolean tryProvider() {
        try {
            EntityType type = EntityType.CREEPER;
            ItemStack is = setEntityType(new ItemStack(Material.MOB_SPAWNER), type);
            EntityType readType = getEntityType(is);
            return type == readType;
        } catch (Throwable t) {
            return false;
        }
    }
}
