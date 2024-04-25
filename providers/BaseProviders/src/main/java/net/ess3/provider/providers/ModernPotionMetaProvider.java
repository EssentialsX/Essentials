package net.ess3.provider.providers;

import net.ess3.provider.PotionMetaProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class ModernPotionMetaProvider implements PotionMetaProvider {
    @Override
    public ItemStack createPotionItem(Material initial, int effectId) {
        throw new UnsupportedOperationException("This should never happen, if this happens please submit a bug report!");
    }

    @Override
    public boolean isSplash(ItemStack stack) {
        return stack.getType() == Material.SPLASH_POTION;
    }

    @Override
    public Collection<PotionEffect> getEffects(ItemStack stack) {
        return ((PotionMeta) stack.getItemMeta()).getCustomEffects();
    }

    @Override
    public String getDescription() {
        return "1.20.5+ Potion Meta Provider";
    }
}
