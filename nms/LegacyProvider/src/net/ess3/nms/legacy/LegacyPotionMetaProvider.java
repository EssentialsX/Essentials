package net.ess3.nms.legacy;

import net.ess3.nms.PotionMetaProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * <p>LegacyPotionMetaProvider class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
@SuppressWarnings("deprecation")
public class LegacyPotionMetaProvider extends PotionMetaProvider {
    /** {@inheritDoc} */
    @Override
    public ItemStack createPotionItem(Material initial, int effectId) {
        ItemStack potion = new ItemStack(initial, 1);
        potion.setDurability((short) effectId);
        return potion;
    }

    /** {@inheritDoc} */
    @Override
    public String getHumanName() {
        return "legacy potion meta provider";
    }
}
