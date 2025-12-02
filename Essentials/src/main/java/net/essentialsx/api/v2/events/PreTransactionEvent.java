package net.essentialsx.api.v2.events;

import com.earth2me.essentials.CommandSource;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;

/**
 * Fired when a transaction (e.g. /pay) is about to be handled.
 */
public class PreTransactionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final CommandSource requester;
    private final IUser target;
    private final BigDecimal amount;
    private boolean cancelled;

    public PreTransactionEvent(final CommandSource requester, final IUser target, final BigDecimal amount) {
        super(!Bukkit.isPrimaryThread());
        this.requester = requester;
        this.target = target;
        this.amount = amount;
    }

    /**
     * @return the user who initiated the transaction
     */
    public CommandSource getRequester() {
        return requester;
    }

    /**
     * @return the user who received the money
     */
    public IUser getTarget() {
        return target;
    }

    /**
     * @return the amount of money transacted
     */
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * If this event should be cancelled. If cancelled, no messages will be displayed to the users involved.
     * @param cancelled whether this event should be cancelled
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
