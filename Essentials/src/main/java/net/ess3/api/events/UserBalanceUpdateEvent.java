package net.ess3.api.events;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;


public class UserBalanceUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BigDecimal originalBalance;
    private BigDecimal balance;
    private Cause cause;

    @Deprecated
    public UserBalanceUpdateEvent(Player player, BigDecimal originalBalance, BigDecimal balance) {
        this(player, originalBalance, balance, Cause.UNKNOWN);
    }

    public UserBalanceUpdateEvent(Player player, BigDecimal originalBalance, BigDecimal balance, Cause cause) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.player = player;
        this.originalBalance = originalBalance;
        this.balance = balance;
        this.cause = cause;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public BigDecimal getNewBalance() {
        return balance;
    }
    
    public void setNewBalance(BigDecimal newBalance) {
        Preconditions.checkNotNull(newBalance, "newBalance cannot be null.");
        this.balance = newBalance;
    }

    public BigDecimal getOldBalance() {
        return originalBalance;
    }

    public Cause getCause() {
        return cause;
    }

    public enum Cause {
        COMMAND_ECO,
        COMMAND_PAY,
        COMMAND_SELL,
        API,
        SPECIAL, // Reserved for API usage
        UNKNOWN
    }
}
