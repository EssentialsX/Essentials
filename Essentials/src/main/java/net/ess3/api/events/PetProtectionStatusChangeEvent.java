package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.HandlerList;

/**
 * Fired when a user's pet protection status is toggled.
 * <p>
 * Note that in older versions (original Essentials and early EssentialsX), the #getAffected
 * and #getController methods are inverted.
 */
public class PetProtectionStatusChangeEvent extends StatusChangeEvent {
    private static final HandlerList handlers = new HandlerList();

    public PetProtectionStatusChangeEvent(final IUser affected, final IUser controller, final boolean value) {
        super(affected, controller, value);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
