package net.ess3.nms.legacy;

import net.ess3.nms.SpawnerProvider;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * <p>LegacySpawnerProvider class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
@SuppressWarnings("deprecation")
public class LegacySpawnerProvider extends SpawnerProvider {
    /** {@inheritDoc} */
    @Override
    public ItemStack setEntityType(ItemStack is, EntityType type) {
        is.getData().setData((byte) type.getTypeId());
        return setDisplayName(is, type);
    }

    /** {@inheritDoc} */
    @Override
    public EntityType getEntityType(ItemStack is) {
        return EntityType.fromId((int) is.getData().getData());
    }

    /** {@inheritDoc} */
    @Override
    public String getHumanName() {
        return "legacy item data provider";
    }
}
