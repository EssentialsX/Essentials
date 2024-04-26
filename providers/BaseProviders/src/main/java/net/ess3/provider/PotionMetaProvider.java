package net.ess3.provider;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Collection;

public interface PotionMetaProvider extends Provider {
    ItemStack createPotionItem(Material initial, int effectId);

    AbstractPotionData getPotionData(ItemStack stack);

    void updatePotionStack(ItemStack stack, AbstractPotionData data);

    interface AbstractPotionData {
        /**
         * Should only be used for pre-flattening
         */
        boolean isSplash();

        /**
         * Should only be used for pre-flattening
         */
        Collection<PotionEffect> getEffects();

        int hashCode();

        PotionType getType();

        void setType(final PotionType type);
    }
}
