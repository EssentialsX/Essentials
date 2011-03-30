package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.command.CommandSender;


public class Commandtp extends EssentialsCommand
{
	public Commandtp()
	{
		super("tp");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		switch (args.length)
		{
		case 0:
			user.sendMessage("§cUsage: /" + commandLabel + " <target> [to-player]");
			return;

		case 1:
			User p = getPlayer(server, args, 0);
			user.teleportCooldown();
			if (!p.isTeleEnabled()) throw new Exception(p.getDisplayName() + " has teleportation disabled.");
			user.sendMessage("§7Teleporting...");
			user.canAfford(this);
			user.teleport(p, this.getName());
			break;

		case 2:
			if (!user.isAuthorized("essentials.tpohere")) throw new Exception("You need access to /tpohere to teleport other players.");
			user.sendMessage("§7Teleporting...");
			user.charge(this);
			User target = getPlayer(server, args, 0);
			User toPlayer = getPlayer(server, args, 1);
			target.teleportToNow(toPlayer);
			target.sendMessage("§7" + user.getDisplayName() + "§7 teleported you to " + toPlayer.getDisplayName() + "§7.");
			break;
		}
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			sender.sendMessage("Usage: /" + commandLabel + " [target] [to-player]");
			return;
		}

		sender.sendMessage("§7Teleporting...");
		User target = getPlayer(server, args, 0);
		User toPlayer = getPlayer(server, args, 1);
		target.teleportToNow(toPlayer);
		target.sendMessage("§7{Console}§7 teleported you to " + toPlayer.getDisplayName() + "§7.");
	}
}
