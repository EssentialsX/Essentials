package net.ess3.api.events.teleport;

import com.earth2me.essentials.ITarget;
import net.ess3.api.IUser;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Called when a command starts a player's teleport warmup.
 * <p>
 * Cancelling this event will prevent the user from teleporting, and any previously pending teleport will commence rather than being cancelled.
 * To skip the warmup delay, see {@link #setDelay(double)}.
 */
public class TeleportWarmupEvent extends TeleportEvent {

    private static final HandlerList handlers = new HandlerList();

    private double delay;

    public TeleportWarmupEvent(final IUser teleporter, final IUser teleportee, final PlayerTeleportEvent.TeleportCause cause, final ITarget target, final double delay) {
        super(teleporter, teleportee, cause, target);
        this.delay = delay;
    }

    public TeleportWarmupEvent(final IUser teleportee, final PlayerTeleportEvent.TeleportCause cause, final ITarget target, final double delay) {
        super(teleportee, cause, target);
        this.delay = delay;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return The warmup delay, in seconds.
     */
    public double getDelay() {
        return delay;
    }

    /**
     * @param delay The warmup delay, in seconds. Set this to 0 to skip the warmup delay.
     */
    public void setDelay(final double delay) {
        this.delay = delay;
    }
}
