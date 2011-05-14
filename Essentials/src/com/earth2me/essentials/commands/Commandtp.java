package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.command.CommandSender;


public class Commandtp extends EssentialsCommand
{
	public Commandtp()
	{
		super("tp");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		switch (args.length)
		{
		case 0:
			throw new NotEnoughArgumentsException();

		case 1:
			User p = getPlayer(server, args, 0);
			if (!p.isTeleportEnabled())
			{
				throw new Exception(Util.format("teleportDisabled", p.getDisplayName()));
			}
			user.sendMessage(Util.i18n("teleporting"));
			user.canAfford(this);
			user.getTeleport().teleport(p, this.getName());
			break;

		case 2:
			if (!user.isAuthorized("essentials.tpohere"))
			{
				throw new Exception("You need access to /tpohere to teleport other players.");
			}
			user.sendMessage(Util.i18n("teleporting"));
			charge(user);
			User target = getPlayer(server, args, 0);
			User toPlayer = getPlayer(server, args, 1);
			target.getTeleport().now(toPlayer);
			target.sendMessage(Util.format("teleportAtoB", user.getDisplayName(), toPlayer.getDisplayName()));
			break;
		}
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		sender.sendMessage(Util.i18n("teleporting"));
		User target = getPlayer(server, args, 0);
		User toPlayer = getPlayer(server, args, 1);
		target.getTeleport().now(toPlayer);
		target.sendMessage(Util.format("teleportAtoB", Console.NAME, toPlayer.getDisplayName()));
	}
}
