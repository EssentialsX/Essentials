package net.ess3.provider.providers;

import net.ess3.provider.PotionMetaProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

@SuppressWarnings("deprecation")
public class PrehistoricPotionMetaProvider implements PotionMetaProvider {
    @Override
    public ItemStack createPotionItem(final Material initial, final int effectId) {
        final ItemStack potion = new ItemStack(initial, 1);
        potion.setDurability((short) effectId);
        return potion;
    }

    @Override
    public boolean isSplash(ItemStack stack) {
        throw new UnsupportedOperationException("This should never happen, if this happens please submit a bug report!");
    }

    @Override
    public Collection<PotionEffect> getEffects(ItemStack stack) {
        throw new UnsupportedOperationException("This should never happen, if this happens please submit a bug report!");
    }

    @Override
    public String getDescription() {
        return "Legacy 1.8 Potion Meta Provider";
    }
}
