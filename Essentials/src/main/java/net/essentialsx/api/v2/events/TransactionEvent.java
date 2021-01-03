package net.essentialsx.api.v2.events;

import com.earth2me.essentials.CommandSource;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;

/**
 * Fired when a transaction (e.g. /pay) is successfully handled.
 */
public class TransactionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final CommandSource requester;
    private final IUser target;
    private final BigDecimal amount;

    public TransactionEvent(CommandSource requester, IUser target, BigDecimal amount) {
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
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
