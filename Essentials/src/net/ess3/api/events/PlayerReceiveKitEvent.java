package net.ess3.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.earth2me.essentials.Kit;
import net.ess3.api.IUser;

public class PlayerReceiveKitEvent extends Event implements Cancellable{

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Kit kit;
    private IUser player;
    
    public PlayerReceiveKitEvent(IUser player, Kit kit) {
    	this.player = player;
    	this.kit = kit;
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
	
	public Kit getKit() {
		return kit;
	}
}

