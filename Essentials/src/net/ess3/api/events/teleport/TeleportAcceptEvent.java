package net.ess3.api.events.teleport;

import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Event that's called when a player executes /tpaccept
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
        System.out.println(sender.getBase().getName() + " " + receiver.getBase().getName());
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
