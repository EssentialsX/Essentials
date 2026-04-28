package net.essentialsx.api.v2.events;

import com.earth2me.essentials.CommandSource;
import com.google.common.base.Preconditions;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;

/**
 * Fired when a transaction (e.g. /pay) is about to be handled.
 */
public class PreTransactionEvent extends TransactionEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    public PreTransactionEvent(final CommandSource requester, final IUser target, final BigDecimal amount) {
        super(!Bukkit.isPrimaryThread(), requester, target, amount);
    }

    /**
     * Sets the amount to be subtracted from the requester's balance.
     * <p>
     * Note: Changing this amount will not verify the requester actually has enough balance to complete the transaction.
     * @param decimal the new amount
     */
    public void setAmount(final BigDecimal decimal) {
        Preconditions.checkNotNull(decimal, "decimal cannot be null");
        Preconditions.checkArgument(decimal.compareTo(BigDecimal.ZERO) >= 0, "decimal cannot be negative");

        this.amount = decimal;
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
