package com.earth2me.essentials.craftbukkit;

import com.earth2me.essentials.IEssentials;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.WorldNBTStorage;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;


public class OfflineBedLocation
{
	public static Location getBedLocation(final String playername, final IEssentials ess)
	{
		try
		{
			final CraftServer cserver = (CraftServer)ess.getServer();
			if (cserver == null)
			{
				return null;
			}
			final WorldNBTStorage wnbtStorage = (WorldNBTStorage)cserver.getHandle().playerFileData;
			if (wnbtStorage == null)
			{
				return null;
			}
			final NBTTagCompound playerStorage = wnbtStorage.getPlayerData(playername);
			if (playerStorage == null)
			{
				return null;
			}

			if (playerStorage.hasKey("SpawnX") && playerStorage.hasKey("SpawnY") && playerStorage.hasKey("SpawnZ"))
			{
				String spawnWorld = playerStorage.getString("SpawnWorld");
				if ("".equals(spawnWorld))
				{
					spawnWorld = cserver.getWorlds().get(0).getName();
				}
				return new Location(cserver.getWorld(spawnWorld), playerStorage.getInt("SpawnX"), playerStorage.getInt("SpawnY"), playerStorage.getInt("SpawnZ"));
			}
			return null;
		}
		catch (Throwable ex)
		{
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
			return null;
		}
	}
}
