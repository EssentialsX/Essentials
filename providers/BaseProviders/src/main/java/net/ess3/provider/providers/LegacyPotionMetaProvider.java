package net.ess3.provider.providers;

import net.ess3.provider.PotionMetaProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
@ProviderData(description = "1.8 Potion Meta Provider")
public class LegacyPotionMetaProvider implements PotionMetaProvider {
    @Override
    public ItemStack createPotionItem(final Material initial, final int effectId) {
        final ItemStack potion = new ItemStack(initial, 1);
        potion.setDurability((short) effectId);
        return potion;
    }
}
