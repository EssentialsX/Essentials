package net.ess3.api.events;

import com.earth2me.essentials.messaging.IMessageRecipient;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called just before a private message is sent to its recipient
 */
public class PrivateMessagePreSendEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private IMessageRecipient sender;
    private IMessageRecipient recipient;
    private String message;

    public PrivateMessagePreSendEvent(IMessageRecipient sender, IMessageRecipient recipient, String message) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
    }

    public IMessageRecipient getSender() {
        return sender;
    }

    public IMessageRecipient getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
