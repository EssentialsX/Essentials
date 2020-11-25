package net.ess3.api.events.teleport;

import com.earth2me.essentials.ITarget;
import net.ess3.api.IUser;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Called before a player is teleported.
 * <p>
 * Note that this is called after any warmup has been performed but before teleport safety checks are performed.
 * Cancelling this event will cancel the teleport without warning the user.
 */
public class PreTeleportEvent extends TeleportEvent {

    private static final HandlerList handlers = new HandlerList();

    public PreTeleportEvent(final IUser teleportee, final PlayerTeleportEvent.TeleportCause cause, final ITarget target) {
        super(teleportee, cause, target);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
