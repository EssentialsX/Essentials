package com.earth2me.essentials.commands;

import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.World;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftWorld;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.TargetBlock;


public class Commandantioch extends EssentialsCommand
{
	public Commandantioch()
	{
		super("antioch");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (!user.isOp())
		{
			user.sendMessage("§cNone shall pass.");
			return;
		}
		
		server.broadcastMessage("...lobbest thou thy Holy Hand Grenade of Antioch towards thy foe,");
		server.broadcastMessage("who being naughty in My sight, shall snuff it.");

		Location loc = user.getLocation();
		World world = ((CraftWorld)user.getWorld()).getHandle();
		loc = new TargetBlock(user).getTargetBlock().getLocation();
		EntityTNTPrimed tnt = new EntityTNTPrimed(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		world.a(tnt);
		world.a(tnt, "random.fuse", 1.0F, 1.0F);
	}
}
