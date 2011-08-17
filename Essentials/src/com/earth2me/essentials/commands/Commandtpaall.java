package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandtpaall extends EssentialsCommand
{
	public Commandtpaall()
	{
		super("tpaall");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			if (sender instanceof Player)
			{
				charge(sender);
				teleportAAllPlayers(server, sender, ess.getUser(sender));
				return;
			}
			throw new NotEnoughArgumentsException();
		}

		User p = getPlayer(server, args, 0);
		charge(sender);
		teleportAAllPlayers(server, sender, p);
	}

	private void teleportAAllPlayers(Server server, CommandSender sender, User p)
	{
		sender.sendMessage(Util.i18n("teleportAAll"));
		for (Player player : server.getOnlinePlayers())
		{
			User u = ess.getUser(player);
			if (p == u)
			{
				continue;
			}
			if (!u.isTeleportEnabled())
			{
				continue;
			}
			try
			{
				u.requestTeleport(p, true);
				u.sendMessage(Util.format("teleportHereRequest", p.getDisplayName()));
			}
			catch (Exception ex)
			{
				ess.showError(sender, ex, getName());
			}
		}
	}
}
