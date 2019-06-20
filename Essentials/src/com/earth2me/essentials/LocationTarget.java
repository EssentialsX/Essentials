package com.earth2me.essentials;

import org.bukkit.Location;


/**
 * <p>LocationTarget class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class LocationTarget implements ITarget {
    private final Location location;

    LocationTarget(Location location) {
        this.location = location;
    }

    /** {@inheritDoc} */
    @Override
    public Location getLocation() {
        return location;
    }
}
