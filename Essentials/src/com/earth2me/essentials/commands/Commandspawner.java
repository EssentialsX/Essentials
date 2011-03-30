package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;

public class Commandspawner extends EssentialsCommand
{
	public Commandspawner()
	{
		super("spawner");
	}

	@Override
	protected void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " [mob]");
			return;
		}

		Block target = user.getTarget().getTargetBlock();
		if (target.getType() != Material.MOB_SPAWNER)
			throw new Exception("Target block must be a mob spawner.");

		try
		{
			((CreatureSpawner)target).setCreatureType(CreatureType.fromName(args[0]));
		}
		catch (Throwable ex)
		{

		}
	}
}
