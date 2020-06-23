package net.ess3.provider;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface SpawnEggProvider extends Provider {
    ItemStack createEggItem(EntityType type) throws IllegalArgumentException;

    EntityType getSpawnedType(ItemStack eggItem) throws IllegalArgumentException;

    @Override
    default boolean tryProvider() {
        try {
            EntityType type = EntityType.CREEPER;
            ItemStack is = createEggItem(type);
            EntityType readType = getSpawnedType(is);
            return type == readType;
        } catch (Throwable t) {
            return false;
        }
    }
}
