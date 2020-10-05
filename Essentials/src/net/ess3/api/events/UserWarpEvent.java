package net.ess3.api.events;

import com.earth2me.essentials.Trade;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when the player use the command /warp
 */
public class UserWarpEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final IUser user;
    private final Trade trade;
    private String warp;
    private boolean cancelled = false;

    public UserWarpEvent(final IUser user, final String warp, final Trade trade) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.user = user;
        this.warp = warp;
        this.trade = trade;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public IUser getUser() {
        return user;
    }

    public String getWarp() {
        return warp;
    }

    public void setWarp(final String warp) {
        this.warp = warp;
    }

    /**
     * Getting payment handling information
     *
     * @return The payment handling class
     */
    public Trade getTrade() {
        return trade;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
