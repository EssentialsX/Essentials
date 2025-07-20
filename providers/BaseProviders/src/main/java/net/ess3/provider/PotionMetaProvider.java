package net.ess3.provider;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Collection;

public interface PotionMetaProvider extends Provider {
    ItemStack createPotionItem(Material initial, int effectId);

    void setSplashPotion(ItemStack stack, boolean isSplash);

    boolean isSplashPotion(ItemStack stack);

    Collection<PotionEffect> getCustomEffects(ItemStack stack);

    boolean isExtended(ItemStack stack);

    boolean isUpgraded(ItemStack stack);

    PotionType getBasePotionType(ItemStack stack);

    void setBasePotionType(ItemStack stack, PotionType type, boolean extended, boolean upgraded);
}
