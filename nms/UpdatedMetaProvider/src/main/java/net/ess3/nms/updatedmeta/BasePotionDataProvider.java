package net.ess3.nms.updatedmeta;

import com.google.common.collect.ImmutableMap;
import net.ess3.nms.PotionMetaProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Map;

public class BasePotionDataProvider extends PotionMetaProvider {
    private static Map<Integer, PotionType> damageValueToType = ImmutableMap.<Integer, PotionType>builder()
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

    @Override
    public ItemStack createPotionItem(Material initial, int effectId) throws IllegalArgumentException {
        ItemStack potion = new ItemStack(initial, 1);

        if (effectId == 0) {
            return potion;
        }

        int damageValue = getBit(effectId, 0) +
                2 * getBit(effectId, 1) +
                4 * getBit(effectId, 2) +
                8 * getBit(effectId, 3);

        PotionType type = damageValueToType.get(damageValue);
        if (type == null) {
            throw new IllegalArgumentException("Unable to process potion effect ID " + effectId + " with damage value " + damageValue);
        }

        boolean extended = getBit(effectId, 6) == 1;
        boolean upgraded = getBit(effectId, 5) == 1;
        boolean splash = getBit(effectId, 14) == 1;

        if (splash && initial == Material.POTION) {
            potion = new ItemStack(Material.SPLASH_POTION, 1);
        }

        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        PotionData data = new PotionData(type, extended, upgraded);
        meta.setBasePotionData(data); // this method is exclusive to recent 1.9+
        potion.setItemMeta(meta);

        return potion;
    }

    private static int getBit(int n, int k) {
        return (n >> k) & 1;
    }

    @Override
    public String getHumanName() {
        return "1.9+ BasePotionData provider";
    }
}
