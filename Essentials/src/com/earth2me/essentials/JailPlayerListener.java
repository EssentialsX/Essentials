package com.earth2me.essentials;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;


public class JailPlayerListener extends PlayerListener
{
	private final IEssentials ess;

	public JailPlayerListener(IEssentials parent)
	{
		this.ess = parent;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		User user = ess.getUser(event.getPlayer());
		if (user.isJailed())
		{
			event.setCancelled(true);
		}
	}
}
