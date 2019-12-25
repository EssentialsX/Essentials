package net.ess3.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.entity.Player;

public final class NewPlayerJoinEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private String playername;
    private boolean cancelled;

    public NewPlayerJoinEvent(Player player) {
    	this.player = player;
      this.playername = player.getName();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
