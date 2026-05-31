package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player's vanish status changes due to the /vanish command.
 * <p>
 * For other cases where the player's vanish status changes, you should listen on PlayerJoinEvent and
 * check with {@link IUser#isVanished()}.
 *
 * <b>WARNING: The values of {@link #getAffected()} and {@link #getController()} are inverted due to
 * a long-standing parameter swap. {@link #getAffected()} returns the command sender (null for console),
 * and {@link #getController()} returns the player whose vanish status changed.</b>
 *
 * @see <a href="https://github.com/EssentialsX/Essentials/issues/2604">#2604</a>
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
