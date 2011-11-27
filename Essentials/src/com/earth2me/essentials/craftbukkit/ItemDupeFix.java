package com.earth2me.essentials.craftbukkit;

import net.minecraft.server.EntityPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;


public class ItemDupeFix extends PlayerListener
{
	@Override
	public void onPlayerTeleport(final PlayerTeleportEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final CraftPlayer player = (CraftPlayer)event.getPlayer();
		final EntityPlayer entity = player.getHandle();
		if (entity.activeContainer != entity.defaultContainer)
		{
			entity.closeInventory();
		}
	}
}
