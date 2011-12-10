package com.earth2me.essentials.api;

import com.earth2me.essentials.Trade;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public interface ITeleport
{
	void now(Location loc, boolean cooldown, TeleportCause cause) throws Exception;
	
	void back(Trade chargeFor) throws Exception;
}
