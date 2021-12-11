package net.ess3.provider.providers;

import net.ess3.provider.ItemUnbreakableProvider;
import org.bukkit.inventory.meta.ItemMeta;

public class LegacyItemUnbreakableProvider implements ItemUnbreakableProvider {
    @Override
    public void setUnbreakable(ItemMeta meta, boolean unbreakable) {
        meta.spigot().setUnbreakable(unbreakable);
    }

    @Override
    public String getDescription() {
        return "Legacy ItemMeta Unbreakable Provider";
    }
}
