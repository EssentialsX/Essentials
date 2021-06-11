package net.essentialsx.api.v2.events;

import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a warp is about to be modified.
 * Includes creation and deletion as described in {@link WarpModifyCause}.
 */
public class WarpModifyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final IUser user;
    private final String warpName;
    private final Location oldLocation;
    private final Location newLocation;
    private final WarpModifyCause cause;
    private boolean cancelled;
    
    /**
     * @param user        the {@link IUser} who is modifing the warp.
     * @param warpName    the name of the warp that's being altered.
     * @param oldLocation the old location before being modified. Null if {@link WarpModifyCause#CREATE}.
     * @param newLocation the new location after being modified. Null if {@link WarpModifyCause#DELETE}.
     * @param cause       the cause of change.
     */
    public WarpModifyEvent(IUser user, String warpName, Location oldLocation, Location newLocation, WarpModifyCause cause) {
        this.user = user;
        this.warpName = warpName;
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
        this.cause = cause;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public IUser getUser() {
        return user;
    }

    public WarpModifyCause getCause() {
        return cause;
    }

    public String getWarpName() {
        return warpName;
    }

    /**
     * Gets the current location of the warp or null if it's being created.
     * @return The warps new location or null.
     */
    public Location getOldLocation() {
        return oldLocation;
    }

    /**
     * Gets the new location this warp is being updated to, or null if it's being deleted.
     * @return The warps new location or null.
     */
    public Location getNewLocation() {
        return newLocation;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * The cause of why a warp was modified.
     * Used by {@link WarpModifyEvent}.
     */
    public enum WarpModifyCause {
        UPDATE, CREATE, DELETE
    }
}
