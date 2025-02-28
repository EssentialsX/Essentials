package net.ess3.api.events;

import com.earth2me.essentials.signs.EssentialsSign;
import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Fired when a player either buys or sells from an essentials sign
 */
public final class SignTransactionEvent extends SignInteractEvent implements Cancellable {
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

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    /**
     * Gets the ItemStack that is about to be bought or sold in this transition.
     * @return The ItemStack being bought or sold.
     */
    public @NotNull ItemStack getItemStack() {
        return itemStack.clone();
    }

    /**
     * Gets the type of transaction, either buy or sell.
     * @return The transaction type.
     */
    public @NotNull TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * Gets the value of the item being bought or sold.
     * @return The item's value.
     */
    public BigDecimal getTransactionValue() {
        return transactionValue;
    }

    public enum TransactionType {
        BUY,
        SELL
    }
}
