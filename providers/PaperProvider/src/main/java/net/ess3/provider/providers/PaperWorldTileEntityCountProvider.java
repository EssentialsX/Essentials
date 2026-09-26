package net.ess3.provider.providers;

import net.ess3.provider.WorldTileEntityCountProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.World;

@ProviderData(description = "Paper 1.11+ World Tile Entity Count Provider", weight = 1)
public class PaperWorldTileEntityCountProvider implements WorldTileEntityCountProvider {
    @Override
    public int getTileEntityCount(World world) {
        return world.getTileEntityCount();
    }
}
