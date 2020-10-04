package net.ess3.api.events;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;

/**
 * Fired when a user's balance updates.
 */
public class UserBalanceUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BigDecimal originalBalance;
    private final Cause cause;
    private BigDecimal balance;

    @Deprecated
    public UserBalanceUpdateEvent(final Player player, final BigDecimal originalBalance, final BigDecimal balance) {
        this(player, originalBalance, balance, Cause.UNKNOWN);
    }

    public UserBalanceUpdateEvent(final Player player, final BigDecimal originalBalance, final BigDecimal balance, final Cause cause) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.player = player;
        this.originalBalance = originalBalance;
        this.balance = balance;
        this.cause = cause;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public BigDecimal getNewBalance() {
        return balance;
    }

    /**
     * Override the value that the user's balance will be changed to.
     *
     * @param newBalance The user's new balance
     */
    public void setNewBalance(final BigDecimal newBalance) {
        Preconditions.checkNotNull(newBalance, "newBalance cannot be null.");
        this.balance = newBalance;
    }

    public BigDecimal getOldBalance() {
        return originalBalance;
    }

    public Cause getCause() {
        return cause;
    }

    /**
     * The cause of the balance update.
     */
    public enum Cause {
        COMMAND_ECO,
        COMMAND_PAY,
        COMMAND_SELL,
        API,
        SPECIAL, // Reserved for API usage
        UNKNOWN
    }
}
