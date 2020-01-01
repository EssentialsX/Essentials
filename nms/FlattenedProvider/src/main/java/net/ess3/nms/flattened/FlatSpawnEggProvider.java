package net.ess3.nms.flattened;

import net.ess3.nms.SpawnEggProvider;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class FlatSpawnEggProvider extends SpawnEggProvider {
    @Override
    public ItemStack createEggItem(EntityType type) throws IllegalArgumentException {
        String name = type.name() + "_SPAWN_EGG";
        Material material = Material.valueOf(name);
        return new ItemStack(material);
    }

    @Override
    public EntityType getSpawnedType(ItemStack eggItem) throws IllegalArgumentException {
        String materialName = eggItem.getType().name();
        if (materialName.contains("_SPAWN_EGG")) {
            return EntityType.valueOf(materialName.replace("_SPAWN_EGG", ""));
        } else {
            throw new IllegalArgumentException("Not a spawn egg");
        }
    }

    @Override
    public String getHumanName() {
        return "1.13+ flat spawn egg provider";
    }
}
