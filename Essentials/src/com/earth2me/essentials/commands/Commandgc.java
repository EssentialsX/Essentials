package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;


public class Commandgc extends EssentialsCommand
{
	public Commandgc()
	{
		super("gc");
	}

	@Override
	protected void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		sender.sendMessage("Maximum memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB");
		sender.sendMessage("Free memory: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MB");
		for (World w : parent.getServer().getWorlds())
		{
			sender.sendMessage(
					(w.getEnvironment() == World.Environment.NETHER ? "Nether" : "World") + " \"" + w.getName() + "\": "
					+ w.getLoadedChunks().length + " chunks, "
					+ w.getEntities().size() + " entities");
		}
	}
}
