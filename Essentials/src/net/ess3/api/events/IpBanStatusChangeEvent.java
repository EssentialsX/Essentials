package net.ess3.api.events;

import net.ess3.api.IUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IpBanStatusChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    String affectedIp;
    IUser controller;
    private String reason;
    private boolean newValue;

    public IpBanStatusChangeEvent(String affectedIp, IUser controller, boolean newValue, String reason) {
        this.affectedIp = affectedIp;
        this.controller = controller;
        this.newValue = newValue;
        this.reason = reason;
    }

    public String getAffectedIp() {
        return affectedIp;
    }

    public IUser getController() {
        return controller;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean getValue() {
        return newValue;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
