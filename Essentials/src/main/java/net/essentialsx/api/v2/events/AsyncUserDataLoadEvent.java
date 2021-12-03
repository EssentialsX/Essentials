package net.essentialsx.api.v2.events;

import net.ess3.api.IUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called during player login after the user's data has been loaded.
 * This is useful for printing login messages once EssentialsX has updated a player's display name.
 */
public class AsyncUserDataLoadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final IUser user;
    private final String joinMessage;

    public AsyncUserDataLoadEvent(IUser user, String joinMessage) {
        super(true);
        this.user = user;
        this.joinMessage = joinMessage;
    }

    /**
     * @return The user whose data has been loaded.
     */
    public IUser getUser() {
        return user;
    }

    /**
     * @return The join message of this user who joined or null if none was displayed.
     */
    public String getJoinMessage() {
        return joinMessage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
