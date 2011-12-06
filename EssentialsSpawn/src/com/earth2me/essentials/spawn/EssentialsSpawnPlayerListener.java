package com.earth2me.essentials.spawn;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;


public class EssentialsSpawnPlayerListener extends PlayerListener
{
	private final transient IEssentials ess;
	private final transient SpawnStorage spawns;

	public EssentialsSpawnPlayerListener(final IEssentials ess, final SpawnStorage spawns)
	{
		super();
		this.ess = ess;
		this.spawns = spawns;
	}

	@Override
	public void onPlayerRespawn(final PlayerRespawnEvent event)
	{
		final User user = ess.getUser(event.getPlayer());

		if (ess.getSettings().getRespawnAtHome())
		{
			Location home = user.getHome(user.getLocation());
			if (home == null)
			{
				home = user.getBedSpawnLocation();
			}
			if (home != null)
			{
				event.setRespawnLocation(home);
				return;
			}
		}
		final Location spawn = spawns.getSpawn(user.getGroup());
		if (spawn != null)
		{
			event.setRespawnLocation(spawn);
		}
	}

	@Override
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final User user = ess.getUser(event.getPlayer());

		if (!user.isNew() || user.getBedSpawnLocation() != null)
		{
			return;
		}
		user.setNew(false);
		if (!"none".equalsIgnoreCase(ess.getSettings().getNewbieSpawn()))
		{
			ess.scheduleSyncDelayedTask(new NewPlayerTeleport(user));
		}

		if (ess.getSettings().getAnnounceNewPlayers())
		{
			ess.broadcastMessage(user, ess.getSettings().getAnnounceNewPlayerFormat(user));
		}
	}


	private class NewPlayerTeleport implements Runnable
	{
		private final transient User user;

		public NewPlayerTeleport(final User user)
		{
			this.user = user;
		}

		@Override
		public void run()
		{
			try
			{
				user.getTeleport().now(spawns.getSpawn(ess.getSettings().getNewbieSpawn()), false);
			}
			catch (Exception ex)
			{
				Bukkit.getLogger().log(Level.WARNING, _("teleportNewPlayerError"), ex);
			}
		}
	}
}
