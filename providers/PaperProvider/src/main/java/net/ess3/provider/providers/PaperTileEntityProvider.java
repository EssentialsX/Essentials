package net.ess3.provider.providers;

import net.ess3.provider.TileEntityProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;

@ProviderData(description = "Paper 1.13+ Tile Entity Provider", weight = 1)
public class PaperTileEntityProvider implements TileEntityProvider {
    @Override
    public BlockState[] getTileEntities(Chunk chunk) {
        return chunk.getTileEntities(false);
    }

    @ProviderTest
    public static boolean test() {
        try {
            Chunk.class.getDeclaredMethod("getTileEntities", boolean.class);
            return true;
        } catch (final NoSuchMethodException ignored) {
            return false;
        }
    }
}
