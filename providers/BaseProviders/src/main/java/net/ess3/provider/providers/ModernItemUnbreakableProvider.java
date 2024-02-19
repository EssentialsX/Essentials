package net.ess3.provider.providers;

import net.ess3.provider.ItemUnbreakableProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.inventory.meta.ItemMeta;

@ProviderData(description = "1.11+ Item Unbreakable Provider", weight = 1)
public class ModernItemUnbreakableProvider implements ItemUnbreakableProvider {
    @Override
    public void setUnbreakable(ItemMeta meta, boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
    }

    @ProviderTest
    public static boolean test() {
        try {
            ItemMeta.class.getDeclaredMethod("setUnbreakable", boolean.class);
            return true;
        } catch (final NoSuchMethodException ignored) {
            return false;
        }
    }
}
