package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import com.earth2me.essentials.User;


public class Commandban extends EssentialsCommand
{
	public Commandban()
	{
		super("ban");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User p = null;
		if (server.matchPlayer(args[0]).isEmpty())
		{
			((CraftServer)server).getHandle().a(args[0]);
			sender.sendMessage("§cPlayer " + args[0] + " banned");
		}
		else
		{
			p = ess.getUser(server.matchPlayer(args[0]).get(0));
			String banReason = "Banned from server";
			if(args.length > 1) {
				banReason = getFinalArg(args, 1);
				p.setBanReason(commandLabel);
			}
			p.kickPlayer(banReason);
			((CraftServer)server).getHandle().a(p.getName());
			sender.sendMessage("§cPlayer " + p.getName() + " banned");
		}
		ess.loadBanList();
	}
}
