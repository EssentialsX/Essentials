package net.ess3.provider.providers;

import net.ess3.provider.WorldTileEntityCountProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.Chunk;
import org.bukkit.World;

@ProviderData(description = "Bukkit World Tile Entity Count Provider")
public class BukkitWorldTileEntityCountProvider implements WorldTileEntityCountProvider {
    @Override
    public int getTileEntityCount(World world) {
        int tileEntities = 0;

        for (final Chunk chunk : world.getLoadedChunks()) {
            tileEntities += chunk.getTileEntities().length;
        }

        return tileEntities;
    }
}
