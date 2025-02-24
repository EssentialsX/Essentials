package com.earth2me.essentials.signs.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SignTransactionEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final ItemStack itemStack;
    private final Player player;
    private final Location signLocation;
    private final TransactionType transactionType;
    private boolean isCancelled = false;

    public SignTransactionEvent(ItemStack itemStack,
                                Player player,
                                Location signLocation,
                                TransactionType transactionType) {
        this.itemStack = itemStack;
        this.player = player;
        this.signLocation = signLocation;
        this.transactionType = transactionType;
    }


    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    /**
     *
     * @return if the event should be cancelled
     */

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    /**
     *
     * @param cancelled sets the event to be cancelled, this will cancel the transaction
     */

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    /**
     *
     * @return a copy of the itemstack in the current transaction
     */

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    /**
     *
     * @return The player activating the transaction
     */

    public Player getPlayer() {
        return player;
    }

    /**
     *
     * @return The sign location where the transaction happened.
     */

    public Location getSignLocation() {
        return signLocation;
    }

    /**
     *
     * @return The transaction type, ether BUY or SELL
     */

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public enum TransactionType {
        BUY,
        SELL
    }
}
