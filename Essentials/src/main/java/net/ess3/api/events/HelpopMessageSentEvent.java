package net.ess3.api.events;

import com.earth2me.essentials.messaging.IMessageRecipient;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called after a message has been sent to the helpop channel.
 */
public class HelpopMessageSentEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final IMessageRecipient sender;
    private final String message;

    public HelpopMessageSentEvent(final IMessageRecipient sender, final String message) {
        this.sender = sender;
        this.message = message;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public IMessageRecipient getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
