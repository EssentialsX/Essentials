package net.ess3.provider.providers;

import net.ess3.provider.SpawnEggProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@ProviderData(description = "1.13+ Spawn Egg Provider", weight = 2)
public class FlatSpawnEggProvider implements SpawnEggProvider {
    @Override
    public ItemStack createEggItem(final EntityType type) throws IllegalArgumentException {
        final Material material = Material.valueOf(type.name() + "_SPAWN_EGG");
        return new ItemStack(material);
    }

    @Override
    public EntityType getSpawnedType(final ItemStack eggItem) throws IllegalArgumentException {
        final String materialName = eggItem.getType().name();
        if (materialName.contains("_SPAWN_EGG")) {
            return EntityType.valueOf(materialName.replace("_SPAWN_EGG", ""));
        }
        throw new IllegalArgumentException("Not a spawn egg");
    }

    @ProviderTest
    public static boolean test() {
        try {
            //noinspection unused
            final Material itMakesMeDeclareAVariable = Material.COW_SPAWN_EGG;
            return true;
        } catch (final Throwable ignored) {
            return false;
        }
    }
}
