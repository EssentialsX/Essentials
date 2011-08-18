package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftCreatureSpawner;
import org.bukkit.entity.CreatureType;


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
		}

		final Block target = user.getTarget().getTargetBlock();
		if (target.getType() != Material.MOB_SPAWNER)
		{
			throw new Exception(Util.i18n("mobSpawnTarget"));
		}

		charge(user);
		try
		{
			String name = args[0].substring(0, 1).toUpperCase() +  args[0].substring(1).toLowerCase();
			if (name.equalsIgnoreCase("Pigzombie")) {
				name = "PigZombie";
			} 
			new CraftCreatureSpawner(target).setCreatureType(CreatureType.fromName(name));
		}
		catch (Throwable ex)
		{
			throw new Exception(Util.i18n("mobSpawnError"), ex);
		}
	}
}
