package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when the player uses the command /tpr
 */
public class UserRandomTeleportEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final IUser user;
    private Location center;
    private double minRange;
    private double maxRange;
    private boolean cancelled = false;

    public UserRandomTeleportEvent(final IUser user, final Location center, final double minRange, final double maxRange) {
        super(!Bukkit.isPrimaryThread());
        this.user = user;
        this.center = center;
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public IUser getUser() {
        return user;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(final Location center) {
        this.center = center;
    }

    public double getMinRange() {
        return minRange;
    }

    public void setMinRange(final double minRange) {
        this.minRange = minRange;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(final double maxRange) {
        this.maxRange = maxRange;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
