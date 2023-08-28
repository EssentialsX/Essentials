package net.essentialsx.api.v2.events;

import com.earth2me.essentials.messaging.IMessageRecipient;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * Called just before a message is sent to the helpop channel.
 */
public class HelpopMessageSendEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final IMessageRecipient sender;
    private List<IMessageRecipient> recipients;
    private final String message;

    public HelpopMessageSendEvent(final IMessageRecipient sender, final List<IMessageRecipient> recipients, final String message) {
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
    public List<IMessageRecipient> getRecipients() {
        return recipients;
    }

    /**
     * Sets the recipients of the helpop message.
     * @param recipients the recipients.
     */
    public void setRecipients(final List<IMessageRecipient> recipients) {
        this.recipients = recipients;
    }

    /**
     * Gets the helpop message to be sent.
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
