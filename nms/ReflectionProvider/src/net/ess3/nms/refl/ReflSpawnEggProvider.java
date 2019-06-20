package net.ess3.nms.refl;

import net.ess3.nms.SpawnEggProvider;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * <p>ReflSpawnEggProvider class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class ReflSpawnEggProvider extends SpawnEggProvider {
    /** {@inheritDoc} */
    @Override
    public ItemStack createEggItem(EntityType type) throws IllegalArgumentException {
        if (ReflUtil.getNMSVersion().startsWith("v1_8_R")) {
            throw new IllegalArgumentException("1.8 servers should use legacy provider");
        }
        try {
            return new SpawnEggRefl(type).toItemStack();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e.getCause());
        }
    }

    /** {@inheritDoc} */
    @Override
    public EntityType getSpawnedType(ItemStack eggItem) throws IllegalArgumentException {
        if (ReflUtil.getNMSVersion().startsWith("v1_8_R")) {
            throw new IllegalArgumentException("1.8 servers should use legacy provider");
        }
        try {
            return SpawnEggRefl.fromItemStack(eggItem).getSpawnedType();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e.getCause());
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getHumanName() {
        return "Reflection based provider";
    }
}
