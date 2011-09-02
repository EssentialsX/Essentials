package com.earth2me.essentials.spawn;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;


public class EssentialsSpawnPlayerListener extends PlayerListener
{
	private final transient IEssentials ess;

	public EssentialsSpawnPlayerListener(IEssentials ess)
	{
		this.ess = ess;
	}

	@Override
	public void onPlayerRespawn(final PlayerRespawnEvent event)
	{
		final User user = ess.getUser(event.getPlayer());

		try
		{
			if (ess.getSettings().getRespawnAtHome())
			{
				Location home = user.getHome(user.getLocation());
				if (home == null)
				{
					throw new Exception();
				}
				event.setRespawnLocation(home);
				return;
			}
		}
		catch (Throwable ex)
		{
		}
		Location spawn = ess.getSpawn().getSpawn(user.getGroup());
		if (spawn == null)
		{
			return;
		}
		event.setRespawnLocation(spawn);
	}

	@Override
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final User user = ess.getUser(event.getPlayer());

		if (!user.isNew())
		{
			return;
		}
		user.setNew(false);
		if (!"none".equalsIgnoreCase(ess.getSettings().getNewbieSpawn()))
		{
			ess.scheduleSyncDelayedTask(new Runnable()
			{
				public void run()
				{
					try
					{
						user.getTeleport().now(ess.getSpawn().getSpawn(ess.getSettings().getNewbieSpawn()));
					}
					catch (Exception ex)
					{
						Logger.getLogger("Minecraft").log(Level.WARNING, Util.i18n("teleportNewPlayerError"), ex);
					}
				}
			});
		}

		if (ess.getSettings().getAnnounceNewPlayers())
		{
			ess.broadcastMessage(user, ess.getSettings().getAnnounceNewPlayerFormat(user));
		}
	}
}
