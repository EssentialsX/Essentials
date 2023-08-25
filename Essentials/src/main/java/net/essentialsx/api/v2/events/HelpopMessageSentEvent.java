package net.essentialsx.api.v2.events;

import com.earth2me.essentials.messaging.IMessageRecipient;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * Called after a message has been sent to the helpop channel.
 */
public class HelpopMessageSentEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final IMessageRecipient sender;
    private final List<? extends IMessageRecipient> recipients;
    private final String message;

    public HelpopMessageSentEvent(final IMessageRecipient sender, final List<? extends IMessageRecipient> recipients, final String message) {
        this.sender = sender;
        this.recipients = recipients;
        this.message = message;
    }

    /**
     * Gets the sender of the helpop message.
     * @return the sender.
     */
    public IMessageRecipient getSender() {
        return sender;
    }

    /**
     * Gets the recipients of the helpop message.
     * @return the recipients.
     */
    public List<? extends IMessageRecipient> getRecipients() {
        return recipients;
    }

    /**
     * Gets the helpop message that was sent.
     * @return the message.
     */
    public String getMessage() {
        return message;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
