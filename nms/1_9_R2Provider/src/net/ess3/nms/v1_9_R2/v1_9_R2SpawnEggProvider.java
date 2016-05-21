package net.ess3.nms.v1_9_R2;

import net.ess3.nms.SpawnEggProvider;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class v1_9_R2SpawnEggProvider extends SpawnEggProvider {
    @Override
    public ItemStack createEggItem(EntityType type) throws IllegalArgumentException {
        return new SpawnEgg1_9_R2(type).toItemStack();
    }

    @Override
    public EntityType getSpawnedType(ItemStack eggItem) throws IllegalArgumentException {
        return SpawnEgg1_9_R2.fromItemStack(eggItem).getSpawnedType();
    }

    @Override
    public String getHumanName() {
        return "CraftBukkit 1.9.4 NMS-based provider";
    }
}
