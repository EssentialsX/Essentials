package com.earth2me.essentials.api;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public interface ITeleport
{
	/**
	 * Used to skip teleport delay when teleporting someone to a location or player.
	 * @param loc
	 * @param cooldown
	 * @param cause
	 * @throws Exception
	 */
	void now(Location loc, boolean cooldown, TeleportCause cause) throws Exception;
}
