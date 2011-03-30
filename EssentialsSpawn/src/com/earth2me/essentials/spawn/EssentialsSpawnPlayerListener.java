package com.earth2me.essentials.spawn;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;


public class EssentialsSpawnPlayerListener extends PlayerListener
{
	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Essentials.loadClasses();
		User user = User.get(event.getPlayer());

		try
		{
			if (Essentials.getSettings().getRespawnAtHome())
			{
				event.setRespawnLocation(user.getHome());
				return;
			}
		}
		catch (Throwable ex)
		{
		}
		event.setRespawnLocation(Essentials.getStatic().spawn.getSpawn(user.getGroup()));
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Essentials.loadClasses();
		User user = User.get(event.getPlayer());
		
		if (!user.isNew()) return;
		user.clearNewFlag();
		try {
			user.teleportToNow(Essentials.getStatic().spawn.getSpawn(Essentials.getSettings().getNewbieSpawn()));
		} catch (Exception ex) {
			Logger.getLogger("Minecraft").log(Level.WARNING, "Failed to teleport new player", ex);
		}

		if (Essentials.getSettings().getAnnounceNewPlayers())
			Essentials.getStatic().getServer().broadcastMessage(Essentials.getSettings().getAnnounceNewPlayerFormat(user));
	}
}
