package com.earth2me.essentials.api;

import com.earth2me.essentials.Trade;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public interface ITeleport
{
	void now(Location loc, boolean cooldown, TeleportCause cause) throws Exception;

	void now(Entity entity, boolean cooldown, TeleportCause cause) throws Exception;

	void back(Trade chargeFor) throws Exception;

	void teleport(Location bed, Trade charge, TeleportCause teleportCause) throws Exception;

	void teleport(Entity entity, Trade chargeFor, TeleportCause cause) throws Exception;

	void home(IUser player, String toLowerCase, Trade charge) throws Exception;

	void respawn(Trade charge, TeleportCause teleportCause) throws Exception;

	void back() throws Exception;

	public void warp(String name, Trade charge, TeleportCause teleportCause) throws Exception;
}
