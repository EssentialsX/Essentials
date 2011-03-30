package com.earth2me.essentials;

import org.bukkit.Server;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;


public class JailPlayerListener extends PlayerListener
{
	private final Server server;
	private final Essentials parent;

	public JailPlayerListener(Essentials parent)
	{
		this.parent = parent;
		this.server = parent.getServer();
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		User user = User.get(event.getPlayer());
		if (user.isJailed())
		{
			event.setCancelled(true);
		}
	}
}
