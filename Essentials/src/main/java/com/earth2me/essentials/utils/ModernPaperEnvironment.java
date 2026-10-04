package com.earth2me.essentials.utils;

import io.papermc.lib.environments.PaperEnvironment;
import io.papermc.lib.features.asyncchunks.AsyncChunksPaper_15;
import io.papermc.lib.features.asyncteleport.AsyncTeleportPaper_13;
import io.papermc.lib.features.bedspawnlocation.BedSpawnLocationPaper;
import io.papermc.lib.features.chunkisgenerated.ChunkIsGeneratedApiExists;

/**
 * A PaperLib environment for Paper servers whose version PaperLib cannot read.
 * <p>
 * PaperLib 1.0.6 expects a single digit major version in {@code Bukkit#getVersion()}, so it reads 26.x as version 0 and
 * selects its synchronous fallbacks for chunk loading, teleporting and bed lookups. All of those exist on every Paper
 * version that reports a year based version.
 */
public class ModernPaperEnvironment extends PaperEnvironment {
    public ModernPaperEnvironment() {
        asyncChunksHandler = new AsyncChunksPaper_15();
        asyncTeleportHandler = new AsyncTeleportPaper_13();
        isGeneratedHandler = new ChunkIsGeneratedApiExists();
        bedSpawnLocationHandler = new BedSpawnLocationPaper();
    }
}
