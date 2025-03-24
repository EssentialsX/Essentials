package net.ess3.api.events;

import com.earth2me.essentials.signs.EssentialsSign;
import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;

/**
 * Fired when a player either buys or sells from an essentials sign
 */
public final class SignTransactionEvent extends SignInteractEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final ItemStack itemStack;
    private final TransactionType transactionType;
    private final BigDecimal transactionValue;
    private boolean isCancelled = false;

    public SignTransactionEvent(EssentialsSign.ISign sign,
                                EssentialsSign essSign,
                                IUser user,
                                @NotNull ItemStack itemStack,
                                @NotNull TransactionType transactionType,
                                BigDecimal transactionValue) {
        super(sign, essSign, user);
        this.itemStack = itemStack;
        this.transactionType = transactionType;
        this.transactionValue = transactionValue;
    }

    /**
     *
     * @return if the event should bee cancelled.
     */

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    /**
     *
     * @param cancelled sets the event to be cancelled, this will cancel the transaction.
     */

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    /**
     *
     * @return a copy of the itemstack in the current transaction.
     */

    public @NotNull ItemStack getItemStack() {
        return itemStack.clone();
    }

    /**
     *
     * @return the type of transaction executed.
     */
    public @NotNull TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     *
     * @return how much was either sold or bought through the sign.
     */

    public BigDecimal getTransactionValue() {
        return transactionValue;
    }

    /**
     * Transaction type of the event
     */

    public enum TransactionType {
        BUY,
        SELL
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
