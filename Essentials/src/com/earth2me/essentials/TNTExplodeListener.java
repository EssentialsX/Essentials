package com.earth2me.essentials;

import com.earth2me.essentials.craftbukkit.FakeExplosion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;


public class TNTExplodeListener extends EntityListener implements Runnable
{
	private final transient IEssentials ess;
	private transient boolean enabled = false;
	private transient int timer = -1;

	public TNTExplodeListener(final IEssentials ess)
	{
		super();
		this.ess = ess;
	}

	public void enable()
	{
		if (!enabled)
		{
			enabled = true;
			timer = ess.scheduleSyncDelayedTask(this, 1000);
			return;
		}
		if (timer != -1)
		{
			ess.getScheduler().cancelTask(timer);
			timer = ess.scheduleSyncDelayedTask(this, 1000);
		}
	}

	@Override
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		if (!enabled)
		{
			return;
		}
		if (event.getEntity() instanceof LivingEntity)
		{
			return;
		}
		FakeExplosion.createExplosion(event, ess.getServer(), ess.getServer().getOnlinePlayers());
		event.setCancelled(true);
	}

	@Override
	public void run()
	{
		enabled = false;
	}
}
