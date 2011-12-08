package com.earth2me.essentials.craftbukkit;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.ChunkCoordinates;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;


public class SetBed
{
	public static void setBed(final Player player, final Block block)
	{
		try
		{
			final CraftPlayer cplayer = (CraftPlayer)player;
			cplayer.getHandle().a(new ChunkCoordinates(block.getX(), block.getY(), block.getZ()));
		}
		catch (Throwable ex)
		{
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
		}
	}
}
