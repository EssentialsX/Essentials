package net.ess3.api.events;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;


/**
 * <p>UserBalanceUpdateEvent class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class UserBalanceUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BigDecimal originalBalance;
    private BigDecimal balance;

    /**
     * <p>Constructor for UserBalanceUpdateEvent.</p>
     *
     * @param player a {@link org.bukkit.entity.Player} object.
     * @param originalBalance a {@link java.math.BigDecimal} object.
     * @param balance a {@link java.math.BigDecimal} object.
     */
    public UserBalanceUpdateEvent(Player player, BigDecimal originalBalance, BigDecimal balance) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.player = player;
        this.originalBalance = originalBalance;
        this.balance = balance;
    }

    /** {@inheritDoc} */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * <p>getHandlerList.</p>
     *
     * @return a {@link org.bukkit.event.HandlerList} object.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * <p>Getter for the field <code>player</code>.</p>
     *
     * @return a {@link org.bukkit.entity.Player} object.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * <p>getNewBalance.</p>
     *
     * @return a {@link java.math.BigDecimal} object.
     */
    public BigDecimal getNewBalance() {
        return balance;
    }
    
    /**
     * <p>setNewBalance.</p>
     *
     * @param newBalance a {@link java.math.BigDecimal} object.
     */
    public void setNewBalance(BigDecimal newBalance) {
        Preconditions.checkNotNull(newBalance, "newBalance cannot be null.");
        this.balance = newBalance;
    }

    /**
     * <p>getOldBalance.</p>
     *
     * @return a {@link java.math.BigDecimal} object.
     */
    public BigDecimal getOldBalance() {
        return originalBalance;
    }
}
