package com.earth2me.essentials.protect;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

@Deprecated
public class EmergencyPlayerListener extends PlayerListener
{

	@Override
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		event.getPlayer().sendMessage("Essentials Protect is in emergency mode. Check your log for errors.");
	}
	
}
