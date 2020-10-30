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
public class TeleportAcceptEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final IUser sender;
    private final IUser receiver;
    private final PlayerTeleportEvent.TeleportCause cause;

    private boolean cancelled;

    public TeleportAcceptEvent(IUser sender, IUser receiver, PlayerTeleportEvent.TeleportCause cause) {
        this.sender = sender;
        this.receiver = receiver;
        this.cause = cause;
    }

    public IUser getSender() {
        return sender;
    }

    public IUser getReceiver() {
        return receiver;
    }

    public PlayerTeleportEvent.TeleportCause getCause() {
        return cause;
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
}
