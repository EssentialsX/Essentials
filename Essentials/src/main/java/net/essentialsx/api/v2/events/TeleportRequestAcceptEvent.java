package net.essentialsx.api.v2.events;

import com.earth2me.essentials.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player accepts a teleport.
 */
public class TeleportRequestAcceptEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final net.ess3.api.IUser requestee;
    private final net.ess3.api.IUser requester;
    private final IUser.TpaRequest tpaRequest;
    private boolean canceled = false;

    public TeleportRequestAcceptEvent(net.ess3.api.IUser requestee, net.ess3.api.IUser requester, IUser.TpaRequest tpaRequest) {
        this.requestee = requestee;
        this.requester = requester;
        this.tpaRequest = tpaRequest;
    }

    /**
     * Gets the user who is accepting this teleport request.
     * @return the user accepting the request.
     */
    public net.ess3.api.IUser getRequestee() {
        return requestee;
    }

    /**
     * Gets the user who submitted this teleport request.
     * @return the user who sent the request.
     */
    public net.ess3.api.IUser getRequester() {
        return requester;
    }

    /**
     * Gets information about this teleport request.
     * @return the {@link com.earth2me.essentials.IUser.TpaRequest} object of this event.
     */
    public IUser.TpaRequest getTpaRequest() {
        return tpaRequest;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
