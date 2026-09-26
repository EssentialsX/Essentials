package com.earth2me.essentials.utils;

import io.papermc.lib.environments.PaperEnvironment;
import io.papermc.lib.features.asyncchunks.AsyncChunksPaper_13;
import io.papermc.lib.features.asyncchunks.AsyncChunksPaper_15;
import io.papermc.lib.features.asyncteleport.AsyncTeleportPaper_13;
import io.papermc.lib.features.bedspawnlocation.BedSpawnLocationPaper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;

public class ModernPaperEnvironment extends PaperEnvironment {
    public ModernPaperEnvironment() {
        super();
        // PaperLib 1.0.6's own version regex only matches a single-digit MC major version and
        // silently zeroes out on 26.x, leaving these handlers on legacy sync fallbacks that
        // crash on Folia's region threading. Re-select them using VersionUtil's own (correct,
        // 26.x-aware) version parsing instead.
        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_13_0_R01)) {
            asyncChunksHandler = new AsyncChunksPaper_13();
            asyncTeleportHandler = new AsyncTeleportPaper_13();
        }
        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_15_2_R01)) {
            try {
                World.class.getDeclaredMethod("getChunkAtAsyncUrgently", Location.class);
                asyncChunksHandler = new AsyncChunksPaper_15();
                HumanEntity.class.getDeclaredMethod("getPotentialBedLocation");
                bedSpawnLocationHandler = new BedSpawnLocationPaper();
            } catch (NoSuchMethodException ignored) {
                // Method doesn't exist on this build; keep the AsyncChunksPaper_13 handler above.
            }
        }
    }
}
