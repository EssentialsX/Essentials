package net.ess3.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.ess3.api.IUser;
import net.ess3.api.LocationData;

/**
 * Called when the player tries to teleport to their home before the
 * location/world is resolved
 */
public class UserTeleportHomeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private IUser user;
    private LocationData target;
    private boolean cancelled;

    public UserTeleportHomeEvent(IUser user, LocationData target) {
        this.user = user;
        this.target = target;
    }

    public IUser getUser() {
        return user;
    }

    public LocationData getLocationData() {
        return target;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
