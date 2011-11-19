package com.earth2me.essentials;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.ChunkPosition;
import net.minecraft.server.Packet60Explosion;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;


public class TNTExplodeListener extends EntityListener implements Runnable
{
	private final IEssentials ess;
	private boolean enabled = false;
	private int timer = -1;

	public TNTExplodeListener(IEssentials ess)
	{
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
		try
		{
			final Set<ChunkPosition> set = new HashSet<ChunkPosition>(event.blockList().size());
			final Player[] players = ess.getServer().getOnlinePlayers();
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
			((CraftServer)ess.getServer()).getHandle().sendPacketNearby(loc.getX(), loc.getY(), loc.getZ(), 64.0, ((CraftWorld)loc.getWorld()).getHandle().worldProvider.dimension, new Packet60Explosion(loc.getX(), loc.getY(), loc.getZ(), 3.0F, set));
		}
		catch (Throwable ex)
		{
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
		}
		event.setCancelled(true);
	}

	@Override
	public void run()
	{
		enabled = false;
	}
}
