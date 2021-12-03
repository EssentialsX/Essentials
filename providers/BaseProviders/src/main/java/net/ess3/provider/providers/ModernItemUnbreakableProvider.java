package net.ess3.provider.providers;

import net.ess3.provider.ItemUnbreakableProvider;
import org.bukkit.inventory.meta.ItemMeta;

public class ModernItemUnbreakableProvider implements ItemUnbreakableProvider {
    @Override
    public void setUnbreakable(ItemMeta meta, boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
    }

    @Override
    public String getDescription() {
        return "1.11+ ItemMeta Unbreakable Provider";
    }
}
