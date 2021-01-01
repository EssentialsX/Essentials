package net.essentialsx.api.v2.events;

import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a warp is about to be modified.
 * Includes creation and deletion as described in {@link WarpModifyCause}.
 */
public class WarpModifyEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final IUser issuer;
    private final String warpName;
    private final WarpModifyCause cause;
    private boolean cancelled;

    /**
     * @param issuer   the {@link IUser} issuing the command
     * @param warpName the name of the warp that's being altered
     * @param cause    the cause of change.
     */
    public WarpModifyEvent(IUser issuer, String warpName, WarpModifyCause cause) {
        this.issuer = issuer;
        this.warpName = warpName;
        this.cause = cause;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public IUser getIssuer() {
        return issuer;
    }

    public WarpModifyCause getCause() {
        return cause;
    }

    public String getWarpName() {
        return warpName;
    }

    /**
     * The cause of why a warp was modified.
     * Used by {@link WarpModifyEvent}.
     */
    public enum WarpModifyCause {
        UPDATE, CREATE, DELETE
    }
}
