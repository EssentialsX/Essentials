package net.ess3.api.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.earth2me.essentials.Kit;

import net.ess3.api.IUser;

/**
 * Called when the player is given a kit
 */
public class KitClaimEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Kit kit;
    private IUser user;
    private boolean cancelled;

    public KitClaimEvent(IUser user, Kit kit) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.user = user;
        this.kit = kit;
    }

    public IUser getUser() {
        return user;
    }

    public Kit getKit() {
        return kit;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
