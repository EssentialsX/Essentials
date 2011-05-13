package com.earth2me.essentials.spawn;

import com.earth2me.essentials.Essentials;
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
	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		User user = Essentials.getStatic().getUser(event.getPlayer());

		try
		{
			if (Essentials.getStatic().getSettings().getRespawnAtHome())
			{
				Location home = user.getHome();
				if (home == null) {
					throw new Exception();
				}
				event.setRespawnLocation(home);
				return;
			}
		}
		catch (Throwable ex)
		{
		}
		event.setRespawnLocation(Essentials.getSpawn().getSpawn(user.getGroup()));
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		User user = Essentials.getStatic().getUser(event.getPlayer());
		
		if (!user.isNew())
		{
			return;
		}
		user.setNew(false);
		try {
			user.getTeleport().now(Essentials.getSpawn().getSpawn(Essentials.getStatic().getSettings().getNewbieSpawn()));
		} catch (Exception ex) {
			Logger.getLogger("Minecraft").log(Level.WARNING, Util.i18n("teleportNewPlayerError"), ex);
		}

		if (Essentials.getStatic().getSettings().getAnnounceNewPlayers())
		{
			Essentials.getStatic().getServer().broadcastMessage(Essentials.getStatic().getSettings().getAnnounceNewPlayerFormat(user));
		}
	}
}
