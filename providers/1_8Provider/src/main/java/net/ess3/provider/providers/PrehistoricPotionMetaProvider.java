package net.ess3.provider.providers;

import net.ess3.provider.PotionMetaProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Collection;

public class PrehistoricPotionMetaProvider implements PotionMetaProvider {
    @Override
    public ItemStack createPotionItem(final Material initial, final int effectId) {
        final ItemStack potion = new ItemStack(initial, 1);
        potion.setDurability((short) effectId);
        return potion;
    }

    @Override
    public void setSplashPotion(final ItemStack stack, final boolean isSplash) {
        if (stack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }

        final Potion potion = Potion.fromItemStack(stack);
        potion.setSplash(isSplash);
        potion.apply(stack);
    }

    @Override
    public boolean isSplashPotion(ItemStack stack) {
        return Potion.fromItemStack(stack).isSplash();
    }

    @Override
    public Collection<PotionEffect> getCustomEffects(ItemStack stack) {
        return Potion.fromItemStack(stack).getEffects();
    }

    @Override
    public boolean isExtended(final ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUpgraded(final ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PotionType getBasePotionType(final ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBasePotionType(final ItemStack stack, final PotionType type, final boolean extended, final boolean upgraded) {
        if (stack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }

        if (extended && upgraded) {
            throw new IllegalArgumentException("Potion cannot be both extended and upgraded");
        }

        final Potion potion = Potion.fromItemStack(stack);

        if (extended && !potion.getType().isInstant()) {
            potion.setHasExtendedDuration(true);
            potion.setLevel(Math.min(potion.getLevel(), 1));
        }

        if (upgraded && type.getMaxLevel() == 2) {
            potion.setLevel(2);
            potion.setHasExtendedDuration(false);
        }

        potion.apply(stack);
    }

    @Override
    public String getDescription() {
        return "Legacy 1.8 Potion Meta Provider";
    }
}
