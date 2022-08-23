package net.ess3.provider.providers;

import net.ess3.provider.SpawnerItemProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

@ProviderData(description = "1.8.3+ Spawner Item Provider")
public class BlockMetaSpawnerItemProvider implements SpawnerItemProvider {
    @Override
    public ItemStack setEntityType(final ItemStack is, final EntityType type) throws IllegalArgumentException {
        final BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
        final BlockState bs = bsm.getBlockState();
        ((CreatureSpawner) bs).setSpawnedType(type);
        bsm.setBlockState(bs);
        is.setItemMeta(bsm);
        return setDisplayName(is, type);
    }

    @Override
    public EntityType getEntityType(final ItemStack is) throws IllegalArgumentException {
        final BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
        final CreatureSpawner bs = (CreatureSpawner) bsm.getBlockState();
        return bs.getSpawnedType();
    }
}
