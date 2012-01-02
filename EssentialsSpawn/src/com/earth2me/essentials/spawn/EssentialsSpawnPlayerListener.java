package com.earth2me.essentials.spawn;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


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
		final IUser user = ess.getUser(event.getPlayer());
		
		boolean respawnAtHome = false;
		ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		try {
			respawnAtHome = ess.getSettings().getData().getCommands().getHome().isRespawnAtHome();
		} finally {
			settings.unlock();
		}
		if (respawnAtHome)
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
		final IUser user = ess.getUser(event.getPlayer());
		user.acquireReadLock();
		try
		{

			if (!user.getData().isNewplayer() || user.getBedSpawnLocation() != null)
			{
				return;
			}
			user.acquireWriteLock();
			user.getData().setNewplayer(false);
		}
		finally
		{
			user.unlock();
		}
		if (spawns.getNewbieSpawn() != null)
		{
			ess.scheduleSyncDelayedTask(new NewPlayerTeleport(user));
		}

		if (spawns.getAnnounceNewPlayers())
		{
			ess.broadcastMessage(user, spawns.getAnnounceNewPlayerFormat(user));
		}
	}


	private class NewPlayerTeleport implements Runnable
	{
		private final transient IUser user;

		public NewPlayerTeleport(final IUser user)
		{
			this.user = user;
		}

		@Override
		public void run()
		{
			try
			{
				Location spawn = spawns.getNewbieSpawn();
				if (spawn != null)
				{
					user.getTeleport().now(spawn, false, TeleportCause.PLUGIN);
				}
			}
			catch (Exception ex)
			{
				Bukkit.getLogger().log(Level.WARNING, _("teleportNewPlayerError"), ex);
			}
		}
	}
}
