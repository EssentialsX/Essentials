package com.earth2me.essentials.listener;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.craftbukkit.FakeExplosion;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;


public class TNTExplodeListener extends EntityListener implements Runnable
{
	private final transient IEssentials ess;
	private transient AtomicBoolean enabled = new AtomicBoolean(false);
	private transient int timer = -1;

	public TNTExplodeListener(final IEssentials ess)
	{
		super();
		this.ess = ess;
	}

	public void enable()
	{
		if (enabled.compareAndSet(false, true))
		{
			timer = ess.scheduleSyncDelayedTask(this, 1000);
			return;
		}
		if (timer != -1)
		{
			ess.getServer().getScheduler().cancelTask(timer);
			timer = ess.scheduleSyncDelayedTask(this, 1000);
		}
	}

	@Override
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		if (!enabled.get())
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
		enabled.set(false);
	}
}
