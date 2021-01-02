package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player's vanish status changes due to the /vanish command.
 * <p>
 * For other cases where the player's vanish status changes, you should listen on PlayerJoinEvent and
 * check with {@link IUser#isVanished()}.
 */
public class VanishStatusChangeEvent extends StatusChangeEvent {
    private static final HandlerList handlers = new HandlerList();

    public VanishStatusChangeEvent(final IUser affected, final IUser controller, final boolean value) {
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
