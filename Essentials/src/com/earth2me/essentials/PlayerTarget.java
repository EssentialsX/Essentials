package com.earth2me.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * <p>PlayerTarget class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class PlayerTarget implements ITarget {
    private final String name;

    /**
     * <p>Constructor for PlayerTarget.</p>
     *
     * @param entity a {@link org.bukkit.entity.Player} object.
     */
    public PlayerTarget(Player entity) {
        this.name = entity.getName();
    }

    /** {@inheritDoc} */
    @Override
    public Location getLocation() {
        return Bukkit.getServer().getPlayerExact(name).getLocation();
    }
}
