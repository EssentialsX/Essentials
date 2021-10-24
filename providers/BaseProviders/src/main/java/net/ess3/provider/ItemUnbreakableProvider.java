package net.ess3.provider;

import org.bukkit.inventory.meta.ItemMeta;

public interface ItemUnbreakableProvider extends Provider {
    void setUnbreakable(ItemMeta meta, boolean unbreakable);
}
