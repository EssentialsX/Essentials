package net.ess3.nms.v1_10_R1;

import net.ess3.nms.SpawnEggProvider;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class v1_10_R1SpawnEggProvider extends SpawnEggProvider {
    @Override
    public ItemStack createEggItem(EntityType type) throws IllegalArgumentException {
        return new SpawnEgg1_10_R1(type).toItemStack();
    }

    @Override
    public EntityType getSpawnedType(ItemStack eggItem) throws IllegalArgumentException {
        return SpawnEgg1_10_R1.fromItemStack(eggItem).getSpawnedType();
    }

    @Override
    public String getHumanName() {
        return "CraftBukkit 1.10 NMS-based provider";
    }
}
