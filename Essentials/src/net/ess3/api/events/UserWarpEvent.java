package net.ess3.api.events;

import com.earth2me.essentials.Trade;
import net.ess3.api.IUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Called when the player use the command /warp
 */
public class UserWarpEvent extends BaseEvent implements Cancellable {

    private IUser user;
    private String warp;
    private Trade trade;
    private boolean cancelled = false;

    public UserWarpEvent(IUser user, String warp, Trade trade){
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
}
