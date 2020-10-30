package net.ess3.api.events.teleport;

import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Called when a player accepts a teleport request.
 * <p>
 * Cancelling this event will prevent the user from teleporting.
 */
public class TeleportRequestAcceptEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final IUser sender;
    private final IUser receiver;
    private final RequestType requestType;

    private boolean cancelled;

    public TeleportRequestAcceptEvent(IUser sender, IUser receiver, RequestType requestType) {
        this.sender = sender;
        this.receiver = receiver;
        this.requestType = requestType;
    }

    public IUser getSender() {
        return sender;
    }

    public IUser getReceiver() {
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
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public enum RequestType {
        TPA,
        TPA_ALL,
        TPA_HERE,
        UNKNOWN
    }
}
