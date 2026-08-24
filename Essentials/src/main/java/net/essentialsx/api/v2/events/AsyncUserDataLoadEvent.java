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
    private final boolean firstJoin;

    public AsyncUserDataLoadEvent(IUser user, String joinMessage, boolean firstJoin) {
        super(true);
        this.user = user;
        this.joinMessage = joinMessage;
        this.firstJoin = firstJoin;
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

    /**
     * Whether this is the first time EssentialsX has seen this player join.
     * <p>
     * This is determined from EssentialsX's own user data rather than {@link org.bukkit.OfflinePlayer#hasPlayedBefore()},
     * which is unreliable on modern server platforms that persist player data during the login/configuration phase.
     *
     * @return {@code true} if this is the player's first recorded join, otherwise {@code false}.
     */
    public boolean isFirstJoin() {
        return firstJoin;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
