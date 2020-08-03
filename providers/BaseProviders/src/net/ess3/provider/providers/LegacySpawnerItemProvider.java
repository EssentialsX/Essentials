package net.ess3.provider.providers;

import net.ess3.provider.SpawnerItemProvider;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class LegacySpawnerItemProvider implements SpawnerItemProvider {
    @Override
    public ItemStack setEntityType(ItemStack is, EntityType type) {
        is.getData().setData((byte) type.getTypeId());
        return setDisplayName(is, type);
    }

    @Override
    public EntityType getEntityType(ItemStack is) {
        return EntityType.fromId((int) is.getData().getData());
    }

    @Override
    public String getDescription() {
        return "legacy item data provider";
    }
}
