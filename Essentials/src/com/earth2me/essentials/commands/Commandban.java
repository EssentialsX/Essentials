package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandban extends EssentialsCommand
{
	public Commandban()
	{
		super("ban");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage("§cUsage: /" + commandLabel + " [player] <reason>");
			return;
		}


		User p = null;
		if (server.matchPlayer(args[0]).isEmpty())
		{
			((CraftServer)server).getHandle().a(args[0]);
			sender.sendMessage("§cPlayer" + args[0] + " banned");
		}
		else
		{
			p = User.get(server.matchPlayer(args[0]).get(0));
			p.kickPlayer(args.length > 1 ? getFinalArg(args, 1) : "Banned from server");
			((CraftServer)server).getHandle().a(p.getName());
			sender.sendMessage("§cPlayer" + p.getName() + " banned");
		}
		Essentials.getStatic().loadBanList();
	}
}
