package net.ess3.api.events;

import com.earth2me.essentials.messaging.IMessageRecipient;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called after a private message has been sent to its recipient.
 *
 * The related private message may not have been successfully received by the recipient,
 * check the MessageResponse with getResponse() to determine the outcome of the delivery attempt.
 */
public class PrivateMessageSentEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private IMessageRecipient sender;
    private IMessageRecipient recipient;
    private String message;
    private IMessageRecipient.MessageResponse response;

    public PrivateMessageSentEvent(IMessageRecipient sender, IMessageRecipient recipient, String message, IMessageRecipient.MessageResponse response) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.response = response;
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

    public IMessageRecipient.MessageResponse getResponse() {
        return response;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
