package net.ess3.provider;

import org.bukkit.Chunk;
import org.bukkit.block.BlockState;

public interface TileEntityProvider extends Provider {
    BlockState[] getTileEntities(Chunk chunk);
}
