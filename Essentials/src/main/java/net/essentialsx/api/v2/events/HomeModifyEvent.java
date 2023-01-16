package net.essentialsx.api.v2.events;

import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a home is about to be modified.
 */
public class HomeModifyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final IUser user;
    private final IUser homeOwner;
    private final Location newLocation;
    private final Location oldLocation;
    private final String newName;
    private final String oldName;
    private final HomeModifyCause cause;
    private boolean canceled = false;

    public HomeModifyEvent(IUser user, IUser homeOwner, String name, Location location, boolean create) {
        this(user, homeOwner,
                create ? location : null, // newLocation
                create ? null : location, // oldLocation
                create ? name : null, // newName
                create ? null : name, // oldName
                create ? HomeModifyCause.CREATE : HomeModifyCause.DELETE);
    }

    public HomeModifyEvent(IUser user, IUser homeOwner, String oldName, String newName, Location location) {
        this(user, homeOwner, location, location, newName, oldName, HomeModifyCause.RENAME);
    }

    public HomeModifyEvent(IUser user, IUser homeOwner, String name, Location oldLocation, Location newLocation) {
        this(user, homeOwner, newLocation, oldLocation, name, name, HomeModifyCause.UPDATE);
    }

    public HomeModifyEvent(IUser user, IUser homeOwner, Location newLocation, Location oldLocation, String newName, String oldName, HomeModifyCause cause) {
        this.user = user;
        this.homeOwner = homeOwner;
        this.newLocation = newLocation;
        this.oldLocation = oldLocation;
        this.newName = newName;
        this.oldName = oldName;
        this.cause = cause;
    }

    /**
     * Gets the user who modified the home or null if the console modified the home.
     * @return The user who modified the home or null.
     */
    public IUser getUser() {
        return user;
    }

    /**
     * Gets the owner of the home being modified.
     * @return The user who owns the home.
     */
    public IUser getHomeOwner() {
        return homeOwner;
    }

    /**
     * Returns the location of the home when {@link #getCause()} returns {@link HomeModifyCause#CREATE} or {@link HomeModifyCause#RENAME},
     * returns the updated location of the home if it returns {@link HomeModifyCause#UPDATE}, or returns null if it returns {@link HomeModifyCause#DELETE}.
     * @return The location of the home or null.
     */
    public Location getNewLocation() {
        return newLocation;
    }

    /**
     * Returns the location of the home when {@link #getCause()} returns {@link HomeModifyCause#RENAME} or {@link HomeModifyCause#DELETE},
     * returns the previous location of the home if it returns {@link HomeModifyCause#UPDATE}, or returns null if it returns {@link HomeModifyCause#CREATE}.
     * @return The location of the home or null.
     */
    public Location getOldLocation() {
        return oldLocation;
    }

    /**
     * Returns the name of the home when {@link #getCause()} returns {@link HomeModifyCause#CREATE} or {@link HomeModifyCause#UPDATE},
     * returns the updated name if it returns {@link HomeModifyCause#RENAME}, or returns null if it returns {@link HomeModifyCause#DELETE}.
     * @return The name of the home or null.
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Returns the name of the home when {@link #getCause()} returns {@link HomeModifyCause#UPDATE} or {@link HomeModifyCause#DELETE},
     * returns the previous name if it returns {@link HomeModifyCause#RENAME}, or returns null if it returns {@link HomeModifyCause#CREATE}.
     * @return The name of the home or null.
     */
    public String getOldName() {
        return oldName;
    }

    /**
     * Returns the underlying cause of this modification to a home.
     * @return The cause.
     */
    public HomeModifyCause getCause() {
        return cause;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * The cause of why a home was modified.
     * Used by {@link HomeModifyEvent}.
     */
    public enum HomeModifyCause {
      CREATE, DELETE, RENAME, UPDATE
    }
}
