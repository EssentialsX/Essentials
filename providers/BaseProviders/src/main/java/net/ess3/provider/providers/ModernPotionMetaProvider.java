package net.ess3.provider.providers;

import net.ess3.provider.PotionMetaProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Collection;

public class ModernPotionMetaProvider implements PotionMetaProvider {
    @Override
    public ItemStack createPotionItem(Material initial, int effectId) {
        throw new UnsupportedOperationException("This should never happen, if this happens please submit a bug report!");
    }

    @Override
    public AbstractPotionData getPotionData(ItemStack stack) {
        return new AbstractPotionData() {
            @Override
            public boolean isSplash() {
                return stack.getType() == Material.SPLASH_POTION;
            }

            @Override
            public Collection<PotionEffect> getEffects() {
                return ((PotionMeta) stack.getItemMeta()).getCustomEffects();
            }

            @Override
            public PotionType getType() {
                return ((PotionMeta) stack.getItemMeta()).getBasePotionType();
            }

            @Override
            public void setType(final PotionType type) {
                ((PotionMeta) stack.getItemMeta()).setBasePotionType(type);
            }

            @Override
            public int hashCode() {
                return stack.getItemMeta().hashCode();
            }
        };
    }

    @Override
    public void updatePotionStack(ItemStack stack, AbstractPotionData data) {
        final PotionMeta meta = (PotionMeta) stack.getItemMeta();
        meta.setBasePotionType(data.getType());
        meta.clearCustomEffects();
        for (PotionEffect effect : data.getEffects()) {
            meta.addCustomEffect(effect, true);
        }
        stack.setItemMeta(meta);

        final AbstractPotionData existing = getPotionData(stack);
        if (existing.isSplash() != data.isSplash()) {
            stack.setType(data.isSplash() ? Material.SPLASH_POTION : Material.POTION);
        }
    }

    @Override
    public String getDescription() {
        return "1.20.5+ Potion Meta Provider";
    }
}
