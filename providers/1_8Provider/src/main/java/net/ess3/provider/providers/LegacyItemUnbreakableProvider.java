package net.ess3.provider.providers;

import net.ess3.provider.ItemUnbreakableProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.inventory.meta.ItemMeta;

@ProviderData(description = "Legacy Item Unbreakable Provider")
public class LegacyItemUnbreakableProvider implements ItemUnbreakableProvider {
    @Override
    public void setUnbreakable(ItemMeta meta, boolean unbreakable) {
        meta.spigot().setUnbreakable(unbreakable);
    }
}
