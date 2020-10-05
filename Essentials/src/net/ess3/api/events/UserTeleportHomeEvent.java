package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a user is teleported home via the /home command.
 * <p>
 * This is called before {@link net.ess3.api.events.teleport.TeleportWarmupEvent TeleportWarmupEvent}.
 */
public class UserTeleportHomeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final IUser user;
    private final String homeName;
    private final Location target;
    private final HomeType homeType;
    private boolean cancelled = false;

    public UserTeleportHomeEvent(final IUser user, final String homeName, final Location target, final HomeType homeType) {
        this.user = user;
        this.homeName = homeName;
        this.target = target;
        this.homeType = homeType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returns the user who is being teleported
     *
     * @return The teleportee.
     */
    public IUser getUser() {
        return user;
    }

    /**
     * Returns the name of the home being teleported to.
     * <p>
     * The behavior of this method varies based on the {@link HomeType} as follows;
     * {@link HomeType#HOME}  - Returns name of home being teleported to.
     * {@link HomeType#BED}   - Returns "bed".
     * {@link HomeType#SPAWN} - Returns null.
     *
     * @return Name of home being teleported to, or null if the user had no homes set.
     */
    public String getHomeName() {
        return homeName;
    }

    /**
     * Returns the location the user is teleporting to.
     *
     * @return Teleportation destination location.
     */
    public Location getHomeLocation() {
        return target;
    }

    /**
     * Returns the home location type.
     * <p>
     * {@link HomeType#HOME}  - A user-set home location.
     * {@link HomeType#BED}   - A user's bed location.
     * {@link HomeType#SPAWN} - The user's current world spawn.
     *
     * @return Home location type.
     */
    public HomeType getHomeType() {
        return homeType;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * The type of home location.
     */
    public enum HomeType {
        HOME,
        BED,
        SPAWN
    }
}
