package net.ess3.provider.providers;

import net.ess3.provider.SpawnEggProvider;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SpawnEgg;

@SuppressWarnings("deprecation")
public class LegacySpawnEggProvider implements SpawnEggProvider {
    @Override
    public ItemStack createEggItem(final EntityType type) throws IllegalArgumentException {
        return new SpawnEgg(type).toItemStack();
    }

    @Override
    public EntityType getSpawnedType(final ItemStack eggItem) throws IllegalArgumentException {
        final MaterialData data = eggItem.getData();
        if (data instanceof SpawnEgg) {
            return ((SpawnEgg) data).getSpawnedType();
        }
        throw new IllegalArgumentException("Item is missing data");
    }

    @Override
    public String getDescription() {
        return "Legacy 1.8 Spawn Egg Provider";
    }
}
