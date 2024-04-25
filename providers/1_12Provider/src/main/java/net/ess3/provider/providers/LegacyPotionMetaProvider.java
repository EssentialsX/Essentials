package net.ess3.provider.providers;

import com.google.common.collect.ImmutableMap;
import net.ess3.provider.PotionMetaProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Collection;
import java.util.Map;

public class LegacyPotionMetaProvider implements PotionMetaProvider {
    private static final Map<Integer, PotionType> damageValueToType = ImmutableMap.<Integer, PotionType>builder()
            .put(1, PotionType.REGEN)
            .put(2, PotionType.SPEED)
            .put(3, PotionType.FIRE_RESISTANCE)
            .put(4, PotionType.POISON)
            .put(5, PotionType.INSTANT_HEAL)
            .put(6, PotionType.NIGHT_VISION)
            // Skip 7
            .put(8, PotionType.WEAKNESS)
            .put(9, PotionType.STRENGTH)
            .put(10, PotionType.SLOWNESS)
            .put(11, PotionType.JUMP)
            .put(12, PotionType.INSTANT_DAMAGE)
            .put(13, PotionType.WATER_BREATHING)
            .put(14, PotionType.INVISIBILITY)
            .build();

    private static int getBit(final int n, final int k) {
        return (n >> k) & 1;
    }

    @Override
    public ItemStack createPotionItem(final Material initial, final int effectId) {
        ItemStack potion = new ItemStack(initial, 1);

        if (effectId == 0) {
            return potion;
        }

        final int damageValue = getBit(effectId, 0) +
                2 * getBit(effectId, 1) +
                4 * getBit(effectId, 2) +
                8 * getBit(effectId, 3);

        final PotionType type = damageValueToType.get(damageValue);
        if (type == null) {
            throw new IllegalArgumentException("Unable to process potion effect ID " + effectId + " with damage value " + damageValue);
        }

        //getBit is splash here
        if (getBit(effectId, 14) == 1 && initial == Material.POTION) {
            potion = new ItemStack(Material.SPLASH_POTION, 1);
        }

        final PotionMeta meta = (PotionMeta) potion.getItemMeta();
        //getBit(s) are extended and upgraded respectfully
        final PotionData data = new PotionData(type, getBit(effectId, 6) == 1, getBit(effectId, 5) == 1);
        meta.setBasePotionData(data); // this method is exclusive to recent 1.9+
        potion.setItemMeta(meta);

        return potion;
    }

    @Override
    public boolean isSplash(ItemStack stack) {
        //noinspection deprecation
        final Potion potion = Potion.fromDamage(stack.getDurability());
        return potion.isSplash();
    }

    @Override
    public Collection<PotionEffect> getEffects(ItemStack stack) {
        //noinspection deprecation
        final Potion potion = Potion.fromDamage(stack.getDurability());
        return potion.getEffects();
    }

    @Override
    public String getDescription() {
        return "1.9-1.20.4 Potion Meta Provider";
    }
}
