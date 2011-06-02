package com.earth2me.essentials.commands;

import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.World;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;


public class Commandnuke extends EssentialsCommand
{
	public Commandnuke()
	{
		super("nuke");
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args)
	{
		Location loc;
		World world;
		server.broadcastMessage("May death rain upon them");
		ess.getTNTListener().enable();
		for (Player player : server.getOnlinePlayers())
		{
			loc = player.getLocation();
			world = ((CraftWorld)loc.getWorld()).getHandle();
			for (int x = -10; x <= 10; x += 5)
			{
				for (int z = -10; z <= 10; z += 5)
				{
					final EntityTNTPrimed tnt = new EntityTNTPrimed(world, loc.getBlockX() + x, 120, loc.getBlockZ() + z);
					world.addEntity(tnt);
					world.makeSound(tnt, "random.fuse", 1.0F, 1.0F);
				}
			}
		}
	}
}
