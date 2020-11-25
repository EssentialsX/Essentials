package net.ess3.api;

import static com.earth2me.essentials.I18n.tl;

/**
 * Fired when trying to teleport a user to an invalid world. This usually only occurs if a world has been removed from
 * the server and a player tries to teleport to a warp or home in that world.
 */
public class InvalidWorldException extends Exception {
    private final String world;

    public InvalidWorldException(final String world) {
        super(tl("invalidWorld"));
        this.world = world;
    }

    public String getWorld() {
        return this.world;
    }
}
