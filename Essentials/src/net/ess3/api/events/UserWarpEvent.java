package net.ess3.api.events;

import com.earth2me.essentials.Trade;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
/**
 * Called when the player use the command /warp
 */
public class UserWarpEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private IUser user;
    private String warp;
    private Trade trade;
    private boolean cancelled = false;


    public UserWarpEvent(IUser user, String warp, Trade trade) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.user = user;
        this.warp = warp;
        this.trade = trade;
    }

    public IUser getUser() {
        return user;
    }

    public String getWarp() {
        return warp;
    }

    /**
     * Getting payment handling information
     * @return The payment handling class
     */
    public Trade getTrade() {
        return trade;
    }

    public void setWarp(String warp) {
        this.warp = warp;
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
