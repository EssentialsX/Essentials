package com.earth2me.essentials.commands;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandworld extends EssentialsCommand
{
	public Commandworld()
	{
		super("world");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		World world;
		List<World> worlds = server.getWorlds();

		if (args.length < 1)
		{
			World nether = server.getWorld(ess.getSettings().getNetherName());
			if (nether == null) {
				for (World world2 : worlds)
				{
					if (world2.getEnvironment() == World.Environment.NETHER) {
						nether = world2;
						break;
					}
				}
				if (nether == null) {
					return;
				}
			}
			world = user.getWorld() == nether ? worlds.get(0) : nether;
		}
		else
		{
			world = ess.getWorld(getFinalArg(args, 0));
			if (world == null)
			{
				user.sendMessage("§cInvalid world.");
				user.sendMessage("§7Possible worlds are the numbers 0 through " + (server.getWorlds().size() - 1) + ".");
				user.sendMessage("§7You can also type the name of a specific world.");
				return;
			}
		}

		double factor;
		if (user.getWorld().getEnvironment() == World.Environment.NETHER && world.getEnvironment() == World.Environment.NORMAL)
		{
			factor = ess.getSettings().getNetherRatio();
		}
		else if (user.getWorld().getEnvironment() != world.getEnvironment())
		{
			factor = 1.0 / ess.getSettings().getNetherRatio();
		}
		else
		{
			factor = 1.0;
		}

		Location loc = user.getLocation();
		loc = new Location(world, loc.getBlockX() * factor + .5, loc.getBlockY(), loc.getBlockZ() * factor + .5);

		user.canAfford(this);
		user.getTeleport().teleport(loc, this.getName());
	}
}
