package net.ess3.provider.providers;

import net.ess3.provider.SpawnEggProvider;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class FlatSpawnEggProvider implements SpawnEggProvider {
    @Override
    public ItemStack createEggItem(EntityType type) throws IllegalArgumentException {
        Material material = Material.valueOf(type.name() + "_SPAWN_EGG");
        return new ItemStack(material);
    }

    @Override
    public EntityType getSpawnedType(ItemStack eggItem) throws IllegalArgumentException {
        String materialName = eggItem.getType().name();
        if (materialName.contains("_SPAWN_EGG")) {
            return EntityType.valueOf(materialName.replace("_SPAWN_EGG", ""));
        }
        throw new IllegalArgumentException("Not a spawn egg");
    }

    @Override
    public String getDescription() {
        return "1.13+ Flattening Spawn Egg Provider";
    }
}
