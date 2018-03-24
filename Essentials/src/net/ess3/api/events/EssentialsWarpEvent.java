package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EssentialsWarpEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private IUser user;
    private String warp;
    private boolean cancelled = false;


    public EssentialsWarpEvent(IUser user, String warp){
        this.user = user;
        this.warp = warp;
    }

    public IUser getUser() {
        return user;
    }

    public String getWarp() {
        return warp;
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
