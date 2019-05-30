package net.ess3.nms.legacy;

import net.ess3.nms.SpawnEggProvider;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SpawnEgg;

@SuppressWarnings("deprecation")
public class LegacySpawnEggProvider extends SpawnEggProvider {
    @Override
    public ItemStack createEggItem(EntityType type) throws IllegalArgumentException {
        return new SpawnEgg(type).toItemStack();
    }

    @Override
    public EntityType getSpawnedType(ItemStack eggItem) throws IllegalArgumentException {
        MaterialData data = eggItem.getData();
        if (data instanceof SpawnEgg) {
            return ((SpawnEgg) data).getSpawnedType();
        } else {
            throw new IllegalArgumentException("Item is missing data");
        }
    }

    @Override
    public String getHumanName() {
        return "legacy item data provider";
    }
}
