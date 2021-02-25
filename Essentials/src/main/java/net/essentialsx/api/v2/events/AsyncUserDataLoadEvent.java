package net.essentialsx.api.v2.events;

import net.ess3.api.IUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when Essentials has finished loading User display data such as their display name.
 */
public class AsyncUserDataLoadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final IUser user;

    public AsyncUserDataLoadEvent(IUser user) {
        super(true);
        this.user = user;
    }

    public IUser getUser() {
        return user;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
