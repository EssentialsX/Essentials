package net.essentialsx.api.v2.events;

import net.ess3.api.IUser;
import net.essentialsx.api.v2.services.mail.MailMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when mail is sent to a {@link net.ess3.api.IUser IUser} by another player or the console.
 * <p>
 * Note: This event has no guarantee of the thread it is fired on, please use {@link #isAsynchronous()}} to see if this event is off the main Bukkit thread.
 */
public class UserMailEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final IUser recipient;
    private final MailMessage message;
    private boolean canceled;

    public UserMailEvent(IUser recipient, MailMessage message) {
        super(!Bukkit.isPrimaryThread());
        this.recipient = recipient;
        this.message = message;
    }

    /**
     * Gets the recipient of this mail.
     * @return the recipient.
     */
    public IUser getRecipient() {
        return recipient;
    }

    /**
     * Gets the underlying {@link MailMessage} for this mail.
     * @return the message.
     */
    public MailMessage getMessage() {
        return message;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
