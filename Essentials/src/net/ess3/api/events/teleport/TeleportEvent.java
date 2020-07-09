package net.ess3.api.events.teleport;

import com.earth2me.essentials.ITarget;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerTeleportEvent;

public abstract class TeleportEvent extends Event implements Cancellable {

    private final IUser teleporter;
    private final IUser teleportee;
    private final PlayerTeleportEvent.TeleportCause cause;
    private final ITarget target;
    private boolean cancelled = false;

    TeleportEvent(IUser teleporter, IUser teleportee, PlayerTeleportEvent.TeleportCause cause, ITarget target) {
        super(!Bukkit.isPrimaryThread());
        this.teleporter = teleporter;
        this.teleportee = teleportee;
        this.cause = cause;
        this.target = target;
    }

    TeleportEvent(IUser teleportee, PlayerTeleportEvent.TeleportCause cause, ITarget target) {
        this(teleportee, teleportee, cause, target);
    }

    /**
     * @return The user that initiated the teleportation, or null if unknown
     */
    public IUser getTeleporter() {
        return teleporter;
    }

    /**
     * @return The user to be teleported
     */
    public IUser getTeleportee() {
        return teleportee;
    }

    /**
     * @return The reason for teleportation
     */
    public PlayerTeleportEvent.TeleportCause getTeleportCause() {
        return cause;
    }

    /**
     * @return The target to teleport to, or null if unknown at this stage (such as a forced respawn)
     */
    public ITarget getTarget() {
        return target;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
