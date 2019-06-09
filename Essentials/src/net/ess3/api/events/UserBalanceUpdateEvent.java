package net.ess3.api.events;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class UserBalanceUpdateEvent extends BaseEvent {
    private final Player player;
    private final BigDecimal originalBalance;
    private BigDecimal balance;

    public UserBalanceUpdateEvent(Player player, BigDecimal originalBalance, BigDecimal balance) {
        this.player = player;
        this.originalBalance = originalBalance;
        this.balance = balance;
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
}
