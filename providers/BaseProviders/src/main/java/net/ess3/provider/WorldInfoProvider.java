package net.ess3.provider;

import org.bukkit.World;

public interface WorldInfoProvider extends Provider {
    /**
     * Gets the maximum safe height for teleportation.
     *
     * @param world The world of which to check the maximum safe height.
     * @return The maximum safe height for teleportation
     */
    int getMaxSafeHeight(World world);

    /**
     * Gets the minimum safe height for teleportation.
     *
     * @param world The world of which to check the base height.
     * @return The minimum safe height for teleportation
     */
    int getMinSafeHeight(World world);
}
