package net.essentialsx.api.v2.events;

import com.earth2me.essentials.AsyncTeleport.TeleportType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player's teleport warmup is cancelled.
 */
public class TeleportWarmupCancelledEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final TeleportType teleportType;
    private final CancelReason cancelReason;
    private final boolean notifyUser;

    public TeleportWarmupCancelledEvent(final Player player, final TeleportType teleportType, final CancelReason cancelReason, final boolean notifyUser) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.teleportType = teleportType;
        this.cancelReason = cancelReason;
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
     * @return the player whose teleport was canceled.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return the type of teleport that was canceled.
     */
    public TeleportType getTeleportType() {
        return this.teleportType;
    }

    /**
     * @return the reason the teleport was cancelled.
     */
    public CancelReason getCancelReason() {
        return this.cancelReason;
    }

    /**
     * @return true if the player was notified that the teleport was canceled, otherwise false.
     */
    public boolean isPlayerNotified() {
        return this.notifyUser;
    }

    /**
     * Indicates the reason why the teleportation was cancelled.
     */
    public enum CancelReason {
        /**
         * Indicates that the cancellation occurred because the player disconnected
         */
        LEAVE,
        /**
         * Indicates that the cancellation occurred because the player moved
         */
        MOVE,
    }
}
