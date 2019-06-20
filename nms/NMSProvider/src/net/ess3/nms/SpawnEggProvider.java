package net.ess3.nms;

import net.ess3.providers.Provider;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * <p>Abstract SpawnEggProvider class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public abstract class SpawnEggProvider implements Provider {
    /**
     * <p>createEggItem.</p>
     *
     * @param type a {@link org.bukkit.entity.EntityType} object.
     * @return a {@link org.bukkit.inventory.ItemStack} object.
     * @throws java.lang.IllegalArgumentException if any.
     */
    public abstract ItemStack createEggItem(EntityType type) throws IllegalArgumentException;
    /**
     * <p>getSpawnedType.</p>
     *
     * @param eggItem a {@link org.bukkit.inventory.ItemStack} object.
     * @return a {@link org.bukkit.entity.EntityType} object.
     * @throws java.lang.IllegalArgumentException if any.
     */
    public abstract EntityType getSpawnedType(ItemStack eggItem) throws IllegalArgumentException;

    /** {@inheritDoc} */
    @Override
    public boolean tryProvider() {
        try {
            EntityType type = EntityType.CREEPER;
            ItemStack is = createEggItem(type);
            EntityType readType = getSpawnedType(is);
            return type == readType;
        } catch (Throwable t) {
            return false;
        }
    }
}
