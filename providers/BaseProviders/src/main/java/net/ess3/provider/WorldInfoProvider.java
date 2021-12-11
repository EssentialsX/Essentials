package net.ess3.provider;

import org.bukkit.World;

public interface WorldInfoProvider extends Provider {
    /**
     * Gets the maximum height of the world.
     *
     * @param world The world of which to check the maximum height.
     * @return The maximum height of the world.
     */
    int getMaxHeight(World world);

    /**
     * Gets the "logical" height of the world, which is the highest Y level at which vanilla spawns Nether portals and
     * performs chorus fruit teleports.
     *
     * @param world The world of which to check the logical height.
     * @return The logical height of the world.
     */
    int getLogicalHeight(World world);

    /**
     * Gets the minimum height of the world.
     *
     * @param world The world of which to check the minimum height.
     * @return The minimum safe height for teleportation
     */
    int getMinHeight(World world);
}
