package com.earth2me.essentials.craftbukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.ChunkPosition;
import net.minecraft.server.Packet60Explosion;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityExplodeEvent;


public class FakeExplosion
{
	public static void createExplosion(final EntityExplodeEvent event, final Server server, final Player[] players)
	{
		try
		{
			final Set<ChunkPosition> set = new HashSet<ChunkPosition>(event.blockList().size());
			final List<ChunkPosition> blocksUnderPlayers = new ArrayList<ChunkPosition>(players.length);
			final Location loc = event.getLocation();
			for (Player player : players)
			{
				if (player.getWorld().equals(loc.getWorld()))
				{
					blocksUnderPlayers.add(new ChunkPosition(player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ()));
				}
			}
			for (Block block : event.blockList())
			{
				final ChunkPosition cp = new ChunkPosition(block.getX(), block.getY(), block.getZ());
				if (!blocksUnderPlayers.contains(cp))
				{
					set.add(cp);
				}
			}
			((CraftServer)server).getHandle().sendPacketNearby(loc.getX(), loc.getY(), loc.getZ(), 64.0, ((CraftWorld)loc.getWorld()).getHandle().worldProvider.dimension, new Packet60Explosion(loc.getX(), loc.getY(), loc.getZ(), 3.0F, set));
		}
		catch (Throwable ex)
		{
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
		}
	}
}
