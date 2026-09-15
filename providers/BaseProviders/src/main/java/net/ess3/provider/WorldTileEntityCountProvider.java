package net.ess3.provider;

import org.bukkit.World;

public interface WorldTileEntityCountProvider extends Provider {
    int getTileEntityCount(final World world);
}
