package net.ess3.nms;

import net.ess3.providers.Provider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class PotionMetaProvider implements Provider {
    public abstract ItemStack createPotionItem(Material initial, int effectId);

    @Override
    public boolean tryProvider() {
        try {
            createPotionItem(Material.POTION, 8260); // Poison Level II Extended
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
