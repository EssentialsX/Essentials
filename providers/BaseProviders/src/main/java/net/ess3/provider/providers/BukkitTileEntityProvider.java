package net.ess3.provider.providers;

import net.ess3.provider.TileEntityProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;

@ProviderData(description = "Bukkit Tile Entity Provider")
public class BukkitTileEntityProvider implements TileEntityProvider {
    @Override
    public BlockState[] getTileEntities(Chunk chunk) {
        return chunk.getTileEntities();
    }
}
