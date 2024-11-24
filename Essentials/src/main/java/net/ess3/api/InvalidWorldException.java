package net.ess3.api;

/**
 * Fired when trying to teleport a user to an invalid world. This usually only occurs if a world has been removed from
 * the server and a player tries to teleport to a warp or home in that world.
 */
public class InvalidWorldException extends TranslatableException {
    private final String world;

    public InvalidWorldException(final String world) {
        super("invalidWorld");
        this.world = world;
    }

    public String getWorld() {
        return this.world;
    }
}
