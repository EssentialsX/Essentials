package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commanddelwarp extends EssentialsCommand
{
	public Commanddelwarp()
	{
		super("delwarp");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /" + commandLabel + " [warp name]");
			return;
		}
		user.charge(this);
		Essentials.getWarps().delWarp(args[0]);
		user.sendMessage("§7Warp removed.");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage("§cUsage: /" + commandLabel + " [warp name]");
			return;
		}

		Essentials.getWarps().delWarp(args[0]);
		sender.sendMessage("§7Warp removed.");
	}
}
