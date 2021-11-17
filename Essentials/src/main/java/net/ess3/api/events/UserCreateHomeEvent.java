package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UserCreateHomeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final IUser user;
    private final String homeName;
    private final Location location;
    private boolean cancelled = false;

    public UserCreateHomeEvent(final IUser user, final String homeName, final Location location) {
        this.user = user;
        this.homeName = homeName;
        this.location = location;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returns the user who is creating the home
     *
     * @return The home creator.
     */
    public IUser getUser() {
        return user;
    }

    /**
     * Returns the name of the home that is being created.
     *
     * @return Name of home being created.
     */
    public String getHomeName() {
        return homeName;
    }

    /**
     * Returns the location the user that is creating the home
     *
     * @return Home location
     */
    public Location getHomeLocation() {
        return location;
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

}
