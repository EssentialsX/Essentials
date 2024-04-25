package net.ess3.provider;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public interface PotionMetaProvider extends Provider {
    ItemStack createPotionItem(Material initial, int effectId);

    /**
     * Should only be used for pre-flattening
     */
    boolean isSplash(ItemStack stack);

    /**
     * Should only be used for pre-flattening
     */
    Collection<PotionEffect> getEffects(ItemStack stack);
}
