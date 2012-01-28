package com.earth2me.essentials.spawn;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class EssentialsSpawnPlayerListener implements Listener
{
	private final transient IEssentials ess;
	private final transient SpawnStorage spawns;

	public EssentialsSpawnPlayerListener(final IEssentials ess, final SpawnStorage spawns)
	{
		super();
		this.ess = ess;
		this.spawns = spawns;
	}

	public void onPlayerRespawn(final PlayerRespawnEvent event)
	{
		final IUser user = ess.getUser(event.getPlayer());

		boolean respawnAtHome = false;
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		try
		{
			respawnAtHome = ess.getSettings().getData().getCommands().getHome().isRespawnAtHome();
		}
		finally
		{
			settings.unlock();
		}
		if (respawnAtHome)
		{
			Location home;
			final Location bed = user.getBedSpawnLocation();
			if (bed != null && bed.getBlock().getType() == Material.BED_BLOCK)
			{
				home = bed;
			}
			else
			{
				home = user.getHome(user.getLocation());
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

	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final IUser user = ess.getUser(event.getPlayer());

		if (user.hasPlayedBefore())
		{
			return;
		}

		if (spawns.getNewbieSpawn() != null)
		{
			ess.scheduleSyncDelayedTask(new NewPlayerTeleport(user), 1L);
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
			if (user.getBase() instanceof OfflinePlayer)
			{
				return;
			}
			
			try
			{
				final Location spawn = spawns.getNewbieSpawn();
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
