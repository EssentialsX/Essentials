package net.ess3.api.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.earth2me.essentials.Teleport.TeleportType;

import net.ess3.api.IUser;

public class PlayerTeleportToEvent extends Event implements Cancellable{

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Location where;
    private IUser player;
    private TeleportType type;
    
    public PlayerTeleportToEvent(IUser player, TeleportType type, Location where) {
    	this.player = player;
    	this.type = type;
    	this.where = where;
    }
    
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean bool) {
		this.cancelled = bool;		
	}
	
	public IUser getPlayer() {
		return player;
	}
	
	public Location getLocation() {
		return where;
	}
	
	public void setLocation(Location loc) {
		this.where = loc;
	}
    
	public TeleportType getType() {
		return type;
	}
}

