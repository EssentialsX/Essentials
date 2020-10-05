package net.ess3.nms.legacy;

import net.ess3.nms.PotionMetaProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class LegacyPotionMetaProvider extends PotionMetaProvider {
    @Override
    public ItemStack createPotionItem(Material initial, int effectId) {
        ItemStack potion = new ItemStack(initial, 1);
        potion.setDurability((short) effectId);
        return potion;
    }

    @Override
    public String getHumanName() {
        return "legacy potion meta provider";
    }
}
