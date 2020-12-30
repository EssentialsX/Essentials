package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player's nickname is changed.
 *
 * <b>WARNING: The values of {@link NickChangeEvent#getAffected()} and {@link NickChangeEvent#getController()} are inverted due to a long-standing implementation bug.</b>
 */
public class NickChangeEvent extends StateChangeEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final String newValue;

    public NickChangeEvent(final IUser affected, final IUser controller, final String value) {
        super(affected, controller);
        this.newValue = value;
    }

    public String getValue() {
        return newValue;
    }

    /**
     * Get the user who CAUSED the state change.
     * <b>WARNING: This method is inverted - this returns the user who <i>caused</i> the change.</b>
     *
     * @return The user who <b>caused the state change</b>.
     */
    @Override
    public IUser getAffected() {
        return super.getAffected();
    }

    /**
     * Get the user who is AFFECTED by the state change.
     * <b>WARNING: This method is inverted - this returns the user who <i>was affected by</i> the change.</b>
     *
     * @return The user who <b>is affected by the state change</b>.
     */
    @Override
    public IUser getController() {
        return super.getController();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
