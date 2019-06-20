package com.earth2me.essentials;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;


/**
 * <p>PlayerExtension class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class PlayerExtension {
    protected Player base;

    /**
     * <p>Constructor for PlayerExtension.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     */
    public PlayerExtension(final Player base) {
        this.base = base;
    }

    /**
     * <p>Getter for the field <code>base</code>.</p>
     *
     * @return a {@link org.bukkit.entity.Player} object.
     */
    public final Player getBase() {
        return base;
    }

    /**
     * <p>Setter for the field <code>base</code>.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     * @return a {@link org.bukkit.entity.Player} object.
     */
    public final Player setBase(final Player base) {
        return this.base = base;
    }

    /**
     * <p>getServer.</p>
     *
     * @return a {@link org.bukkit.Server} object.
     */
    public Server getServer() {
        return base.getServer();
    }

    /**
     * <p>getWorld.</p>
     *
     * @return a {@link org.bukkit.World} object.
     */
    public World getWorld() {
        return base.getWorld();
    }

    /**
     * <p>getLocation.</p>
     *
     * @return a {@link org.bukkit.Location} object.
     */
    public Location getLocation() {
        return base.getLocation();
    }
}
