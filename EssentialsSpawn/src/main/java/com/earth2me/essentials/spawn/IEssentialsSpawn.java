package com.earth2me.essentials.spawn;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public interface IEssentialsSpawn extends Plugin {

    /**
     * Sets the spawn for a given group to a given location.
     *
     * @param loc   The location to set the spawn to
     * @param group The group to set the spawn of, or 'default' for the default spawn
     * @throws IllegalArgumentException If group is null
     */
    void setSpawn(Location loc, String group);

    /**
     * Gets the spawn location for a given group.
     *
     * @param group The group to get the spawn of, or 'default' for the default spawn
     * @return The spawn location set for the given group
     * @throws IllegalArgumentException If group is null
     */
    Location getSpawn(String group);
}
