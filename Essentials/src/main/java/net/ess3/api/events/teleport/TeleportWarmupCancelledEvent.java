package net.ess3.api.events.teleport;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.earth2me.essentials.AsyncTeleport.TeleportType;

/**
 * Called when a player's teleport warmup is cancelled.
 */
public class TeleportWarmupCancelledEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final TeleportType teleportType;
    private final boolean notifyUser;

    public TeleportWarmupCancelledEvent(final Player player, final TeleportType teleportType, final boolean notifyUser) {
        this.player = player;
        this.teleportType = teleportType;
        this.notifyUser = notifyUser;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return The player object
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return The teleport type
     */
    public TeleportType getTeleportType() {
        return this.teleportType;
    }

    /**
     * @return Is the player notified?
     */
    public boolean isPlayerNotified() {
        return this.notifyUser;
    }
}
