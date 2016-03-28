package net.ess3.nms;

import net.ess3.providers.Provider;
import org.bukkit.inventory.ItemStack;

public abstract class PotionMetaProvider implements Provider {
    public abstract ItemStack createPotionItem(int effectId);

    @Override
    public boolean tryProvider() {
        try {
            createPotionItem(16420); // Poison Level II Splash
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
