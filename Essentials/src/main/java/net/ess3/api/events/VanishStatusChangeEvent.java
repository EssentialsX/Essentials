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

    public boolean isFakeJoinMessage() {
        return fakeJoinMessage != null;
    }

    public String getFakeJoinMessage() {
        if (isFakeJoinMessage()) {
            return tl("unvanishBroadcast", affected.getName(), affected.getDisplayName());
        }
        return null;
    }

    public void setFakeJoinMessage(String message) {
        fakeJoinMessage = message;
    }

    public boolean isFakeLeaveMessage() {
        return fakeLeaveMessage != null;
    }

    public String getFakeLeaveMessage() {
        if (isFakeLeaveMessage()) {
            return tl("vanishBroadcast", affected.getName(), affected.getDisplayName());
        }
        return null;
    }

    public void setFakeLeaveMessage(String message) {
        fakeLeaveMessage = message;
    }
}
