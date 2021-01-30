package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.HandlerList;

import static com.earth2me.essentials.I18n.tl;

/**
 * Fired when a player's vanish status changes due to the /vanish command.
 * <p>
 * For other cases where the player's vanish status changes, you should listen on PlayerJoinEvent and
 * check with {@link IUser#isVanished()}.
 */
public class VanishStatusChangeEvent extends StatusChangeEvent {
    private static final HandlerList handlers = new HandlerList();
    private String fakeJoinMessage = null;
    private String fakeLeaveMessage = null;

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

    /**
     * @return Is this vanish change event causing a fake join message.
     */
    public boolean isFakeJoinMessage() {
        return fakeJoinMessage != null;
    }

    /**
     * @return The default fake join message from tl if isFakeJoinMessage returns true.
     */
    public String getFakeJoinMessage() {
        if (isFakeJoinMessage()) {
            return tl("unvanishBroadcast", affected.getName(), affected.getDisplayName());
        }
        return null;
    }

    /**
     * @param message Sets the fake join message.
     */
    public void setFakeJoinMessage(String message) {
        fakeJoinMessage = message;
    }

    /**
     * @return Is this vanish change event causing a fake leave message.
     */
    public boolean isFakeLeaveMessage() {
        return fakeLeaveMessage != null;
    }

    /**
     * @return The default fake leave message from tl if isFakeLeaveMessage returns true.
     */
    public String getFakeLeaveMessage() {
        if (isFakeLeaveMessage()) {
            return tl("vanishBroadcast", affected.getName(), affected.getDisplayName());
        }
        return null;
    }

    /**
     * @param message Sets the fake leave message.
     */
    public void setFakeLeaveMessage(String message) {
        fakeLeaveMessage = message;
    }
}
