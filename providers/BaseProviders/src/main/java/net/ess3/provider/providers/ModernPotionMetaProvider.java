package net.ess3.provider.providers;

import net.ess3.provider.PotionMetaProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Collection;

@ProviderData(description = "1.20.6+ Potion Meta Provider", weight = 2)
public class ModernPotionMetaProvider implements PotionMetaProvider {
    @Override
    public ItemStack createPotionItem(Material initial, int effectId) {
        throw new UnsupportedOperationException("This should never happen, if this happens please submit a bug report!");
    }

    @Override
    public void setBasePotionType(final ItemStack stack, PotionType type, final boolean extended, final boolean upgraded) {
        if (stack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }

        if (extended && upgraded) {
            throw new IllegalArgumentException("Potion cannot be both extended and upgraded");
        }

        final String name = type.name();
        if (name.startsWith("LONG_")) {
            type = PotionType.valueOf(name.substring(5));
        } else if (name.startsWith("STRONG_")) {
            type = PotionType.valueOf(name.substring(7));
        }

        if (extended && type.isExtendable()) {
            type = PotionType.valueOf("LONG_" + type.name());
        }

        if (upgraded && type.isUpgradeable()) {
            type = PotionType.valueOf("STRONG_" + type.name());
        }

        final PotionMeta meta = (PotionMeta) stack.getItemMeta();
        //noinspection DataFlowIssue
        meta.setBasePotionType(type);
        stack.setItemMeta(meta);
    }

    @Override
    public Collection<PotionEffect> getCustomEffects(ItemStack stack) {
        final PotionMeta meta = (PotionMeta) stack.getItemMeta();
        //noinspection DataFlowIssue
        return meta.getCustomEffects();
    }

    @Override
    public boolean isSplashPotion(ItemStack stack) {
        return stack != null && stack.getType() == Material.SPLASH_POTION;
    }

    @Override
    public boolean isExtended(ItemStack stack) {
        final PotionMeta meta = (PotionMeta) stack.getItemMeta();
        //noinspection DataFlowIssue
        return meta.getBasePotionType().name().startsWith("LONG_");
    }

    @Override
    public boolean isUpgraded(ItemStack stack) {
        final PotionMeta meta = (PotionMeta) stack.getItemMeta();
        //noinspection DataFlowIssue
        return meta.getBasePotionType().name().startsWith("STRONG_");
    }

    @Override
    public PotionType getBasePotionType(ItemStack stack) {
        final PotionMeta meta = (PotionMeta) stack.getItemMeta();
        //noinspection DataFlowIssue
        PotionType type = meta.getBasePotionType();
        //noinspection DataFlowIssue
        final String name = type.name();
        if (name.startsWith("LONG_")) {
            type = PotionType.valueOf(name.substring(5));
        } else if (name.startsWith("STRONG_")) {
            type = PotionType.valueOf(name.substring(7));
        }
        return type;
    }

    @Override
    public void setSplashPotion(ItemStack stack, boolean isSplash) {
        if (stack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }

        if (isSplash && stack.getType() == Material.POTION) {
            stack.setType(Material.SPLASH_POTION);
        } else if (!isSplash && stack.getType() == Material.SPLASH_POTION) {
            stack.setType(Material.POTION);
        }
    }

    @ProviderTest
    public static boolean test() {
        try {
            // This provider was created due to Potion being removed in 1.20.6
            Class.forName("org.bukkit.potion.Potion");
            return false;
        } catch (final Throwable ignored) {
            return true;
        }
    }
}
