package net.essentialsx.api.v2.events;

import net.ess3.api.IUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Called when items from a kit are about to be given to a {@link IUser user}.
 * <p>
 * This event is not cancellable and is called right before items are about to be received by the {@link #getUser() user}.
 * If you want to prevent kits from being claimed to begin with, use the {@link net.ess3.api.events.KitClaimEvent}
 * @see net.ess3.api.events.KitClaimEvent
 */
public class PreKitExpandItemsEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final IUser user;
    private final String kitName;
    private final List<ItemStack> itemStacks;

    public PreKitExpandItemsEvent(IUser user, String kitName, List<ItemStack> itemStacks) {
        this.user = user;
        this.kitName = kitName;
        this.itemStacks = itemStacks;
    }

    /**
     * Gets the {@link IUser user} who is receiving the kit.
     * @return the user.
     */
    public IUser getUser() {
        return user;
    }

    /**
     * Gets the name of the kit the {@link IUser user} is receiving.
     * @return the name of the kit.
     */
    public String getKitName() {
        return kitName;
    }

    /**
     * Returns a mutable list with items to be received by the {@link IUser user}.
     * @return the list of items.
     */
    public List<ItemStack> getItemStacks() {
        return itemStacks;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
