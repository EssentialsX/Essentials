package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a user is teleported home via the /home command.
 *
 * This is called before {@link net.ess3.api.events.UserTeleportEvent UserTeleportEvent}.
 */
public class UserTeleportHomeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final IUser user;
    private final Location target;
    private final HomeType homeType;
    private boolean cancelled = false;

    public UserTeleportHomeEvent(IUser user, Location target, HomeType homeType) {
        this.user = user;
        this.target = target;
        this.homeType = homeType;
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
     * Returns the location the user is teleporting to.
     *
     * @return Teleportation destination location.
     */
    public Location getHomeLocation() {
        return target;
    }

    /**
     * Returns the home location type.
     *
     * {@link HomeType#HOME}    - A user-set home location.
     * {@link HomeType#BED}     - A user's bed location.
     * {@link HomeType#RESPAWN} - A user's bed location, if set. Otherwise, the world spawn.
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
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum HomeType {
        HOME,
        BED,
        RESPAWN
    }
}
