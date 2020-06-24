package net.ess3.provider.providers;

import net.ess3.provider.SpawnerProvider;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class BlockMetaSpawnerProvider implements SpawnerProvider {
    @Override
    public ItemStack setEntityType(ItemStack is, EntityType type) throws IllegalArgumentException {
        BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
        BlockState bs = bsm.getBlockState();
        ((CreatureSpawner) bs).setSpawnedType(type);
        bsm.setBlockState(bs);
        is.setItemMeta(bsm);
        return setDisplayName(is, type);
    }

    @Override
    public EntityType getEntityType(ItemStack is) throws IllegalArgumentException {
        BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
        CreatureSpawner bs = (CreatureSpawner) bsm.getBlockState();
        return bs.getSpawnedType();
    }

    @Override
    public String getDescription() {
        return "1.8.3+ Spawner Provider";
    }
}
