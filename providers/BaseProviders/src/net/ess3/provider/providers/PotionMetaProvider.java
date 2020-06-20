package net.ess3.provider.providers;

import net.ess3.provider.Provider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface PotionMetaProvider extends Provider {
    ItemStack createPotionItem(Material initial, int effectId);

    @Override
    default boolean tryProvider() {
        try {
            createPotionItem(Material.POTION, 8260); // Poison Level II Extended
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
