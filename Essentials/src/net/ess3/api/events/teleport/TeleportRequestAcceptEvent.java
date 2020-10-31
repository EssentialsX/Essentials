package net.ess3.api.events.teleport;

import com.earth2me.essentials.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player accepts a teleport request.
 * <p>
 * Cancelling this event will prevent the user from teleporting.
 */
public class TeleportRequestAcceptEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final User sender;
    private final User receiver;
    private final RequestType requestType;

    private boolean cancelled;

    public TeleportRequestAcceptEvent(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.requestType = receiver.getRequestType();
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
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

    /**
     * Used to determine what teleport occured
     */
    public enum RequestType {
        TPA,
        TPA_ALL,
        TPA_HERE,
        UNKNOWN
    }
}
