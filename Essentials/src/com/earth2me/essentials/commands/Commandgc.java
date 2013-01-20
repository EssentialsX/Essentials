package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import java.lang.management.ManagementFactory;
import java.util.List;
import org.bukkit.ChatColor;
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
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		float tps = ess.getTimer().getAverageTPS();
		ChatColor color;
		if (tps >= 18)
		{
			color = ChatColor.GREEN;
		}
		else if (tps >= 15)
		{
			color = ChatColor.YELLOW;
		}
		else
		{
			color = ChatColor.RED;
		}

		sender.sendMessage(_("uptime", Util.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime())));
		sender.sendMessage(_("tps", "" + color + tps));
		sender.sendMessage(_("gcmax", (Runtime.getRuntime().maxMemory() / 1024 / 1024)));
		sender.sendMessage(_("gctotal", (Runtime.getRuntime().totalMemory() / 1024 / 1024)));
		sender.sendMessage(_("gcfree", (Runtime.getRuntime().freeMemory() / 1024 / 1024)));

		List<World> worlds = server.getWorlds();
		if (worlds.size() > 3 && args.length == 0)
		{
			sender.sendMessage(_("messageTruncated", commandLabel, "all"));
		}
		else
		{
			for (World w : worlds)
			{
				String worldType = "World";
				switch (w.getEnvironment())
				{
				case NETHER:
					worldType = "Nether";
					break;
				case THE_END:
					worldType = "The End";
					break;
				}

				sender.sendMessage(_("gcWorld", worldType, w.getName(), w.getLoadedChunks().length, w.getEntities().size()));
			}
		}

	}
}
