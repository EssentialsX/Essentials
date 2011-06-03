package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
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
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		Block target = user.getTarget().getTargetBlock();
		if (target.getType() != Material.MOB_SPAWNER)
		{
			throw new Exception(Util.i18n("mobSpawnTarget"));
		}

		charge(user);
		try
		{
			new CraftCreatureSpawner(target).setCreatureType(CreatureType.fromName(args[0]));
		}
		catch (Throwable ex)
		{
			throw new Exception(Util.i18n("mobSpawnError"), ex);
		}
	}
}
