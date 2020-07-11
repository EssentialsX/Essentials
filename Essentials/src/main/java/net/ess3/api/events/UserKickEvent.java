package net.ess3.api.events;

import com.earth2me.essentials.IUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UserKickEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private IUser kicked;
    private IUser kicker;
    private String reason;
    private boolean cancelled;

    public UserKickEvent(IUser kicked, IUser kicker, String reason) {
        super(!Bukkit.isPrimaryThread());
        this.kicked = kicked;
        this.kicker = kicker;
        this.reason = reason;
    }

    public IUser getKicked() {
        return kicked;
    }

    public IUser getKicker() {
        return kicker;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
