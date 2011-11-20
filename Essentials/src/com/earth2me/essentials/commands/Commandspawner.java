package com.earth2me.essentials.commands;

import com.earth2me.essentials.Mob;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;


public class Commandspawner extends EssentialsCommand
{
	public Commandspawner()
	{
		super("spawner");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1 || args[0].length() < 2)
		{
			throw new NotEnoughArgumentsException();
			//TODO: user.sendMessage("§7Mobs: Zombie PigZombie Skeleton Slime Chicken Pig Monster Spider Creeper Ghast Squid Giant Cow Sheep Wolf");
		}

		final Block target = user.getTarget().getTargetBlock();
		if (target.getType() != Material.MOB_SPAWNER)
		{
			throw new Exception(Util.i18n("mobSpawnTarget"));
		}

		try
		{
			String name = args[0];

			Mob mob = null;
			mob = Mob.fromName(name);
			if (mob == null)
			{
				user.sendMessage(Util.i18n("invalidMob"));
				return;
			}
			((CreatureSpawner)target.getState()).setCreatureType(mob.getType());
			user.sendMessage(Util.format("setSpawner", mob.name));
		}
		catch (Throwable ex)
		{
			throw new Exception(Util.i18n("mobSpawnError"), ex);
		}
	}
}
