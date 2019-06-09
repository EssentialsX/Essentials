package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
/**
 * Called when the player teleports
 */
public class UserTeleportEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private IUser user;
    private TeleportCause cause;
    private boolean cancelled = false;

    public UserTeleportEvent(IUser user, TeleportCause cause) {
        this.user = user;
        this.cause = cause;
    }

    public IUser getUser() {
        return user;
    }

    public TeleportCause getTeleportCause() {
        return cause;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
