package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;


public class Commandbreak extends EssentialsCommand
{
	public Commandbreak()
	{
		super("break");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		Block block = user.getTargetBlock(null, 20);
		if (block.getType() == Material.AIR)
		{
			throw new NoChargeException();
		}
		if (block.getType() == Material.BEDROCK && !user.isAuthorized("essentials.break.bedrock"))
		{
			throw new NoChargeException();
		}
		BlockBreakEvent event = new BlockBreakEvent(block, user);
		server.getPluginManager().callEvent(event);
		if (event.isCancelled())
		{
			throw new NoChargeException();
		}
		else
		{
			block.setType(Material.AIR);
		}
	}
}