package com.earth2me.essentials.protect;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;


public class EmergencyEntityListener extends EntityListener
{

	@Override
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		event.setCancelled(true);
	}

	@Override
	public void onEntityDamage(final EntityDamageEvent event)
	{
		event.setCancelled(true);
	}
}
