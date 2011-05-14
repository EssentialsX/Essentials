package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
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
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		charge(sender);
		sender.sendMessage(Util.format("gcmax", (Runtime.getRuntime().maxMemory() / 1024 / 1024)));
		sender.sendMessage(Util.format("gcmin", (Runtime.getRuntime().freeMemory() / 1024 / 1024)));
		for (World w : server.getWorlds())
		{
			sender.sendMessage(
					(w.getEnvironment() == World.Environment.NETHER ? "Nether" : "World") + " \"" + w.getName() + "\": "
					+ w.getLoadedChunks().length + Util.i18n("gcchunks")
					+ w.getEntities().size() + Util.i18n("gcentities"));
		}
	}
}
