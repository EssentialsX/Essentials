package net.ess3.nms;

import net.ess3.providers.Provider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * <p>Abstract PotionMetaProvider class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public abstract class PotionMetaProvider implements Provider {
    /**
     * <p>createPotionItem.</p>
     *
     * @param initial a {@link org.bukkit.Material} object.
     * @param effectId a int.
     * @return a {@link org.bukkit.inventory.ItemStack} object.
     */
    public abstract ItemStack createPotionItem(Material initial, int effectId);

    /** {@inheritDoc} */
    @Override
    public boolean tryProvider() {
        try {
            createPotionItem(Material.POTION, 8260); // Poison Level II Extended
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
