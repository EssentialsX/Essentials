package com.earth2me.essentials;

import org.bukkit.Server;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;


public class EssentialsEntityListener extends EntityListener
{
	private final Server server;
	private final Essentials parent;

	public EssentialsEntityListener(Essentials parent)
	{
		this.parent = parent;
		this.server = parent.getServer();
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event instanceof EntityDamageEvent || event instanceof EntityDamageByBlockEvent || event instanceof EntityDamageByProjectileEvent)
		{

			if (event.getEntity() instanceof Player && User.get(event.getEntity()).isGodModeEnabled())
			{
				CraftPlayer player = (CraftPlayer)event.getEntity();
				player.getHandle().fireTicks = 0;
				player.setRemainingAir(player.getMaximumAir());
				event.setCancelled(true);
			}
		}
	}

	
	@Override
	public void onEntityCombust(EntityCombustEvent event)
	{
		if (event.getEntity() instanceof Player && User.get(event.getEntity()).isGodModeEnabled())
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			User user = User.get(event.getEntity());
			if(user.isAuthorized("essentials.back.ondeath"))
			{
			user.lastLocation = user.getLocation();
			user.sendMessage("§7Use the /back command to return to your death point");
			}
		}
	}

}
