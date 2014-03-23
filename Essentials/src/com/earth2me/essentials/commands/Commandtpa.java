package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandtpa extends EssentialsCommand
{
	public Commandtpa()
	{
		super("tpa");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User player = getPlayer(server, user, args, 0);
		if (user.getName().equalsIgnoreCase(player.getName()))
		{
			throw new NotEnoughArgumentsException();
		}
		if (!player.isTeleportEnabled())
		{
			throw new Exception(tl("teleportDisabled", player.getDisplayName()));
		}
		if (user.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions()
			&& !user.isAuthorized("essentials.worlds." + player.getWorld().getName()))
		{
			throw new Exception(tl("noPerm", "essentials.worlds." + player.getWorld().getName()));
		}
		if (!player.isIgnoredPlayer(user))
		{
			player.requestTeleport(user, false);
			player.sendMessage(tl("teleportRequest", user.getDisplayName()));
			player.sendMessage(tl("typeTpaccept"));
			player.sendMessage(tl("typeTpdeny"));
			if (ess.getSettings().getTpaAcceptCancellation() != 0)
			{
				player.sendMessage(tl("teleportRequestTimeoutInfo", ess.getSettings().getTpaAcceptCancellation()));
			}
		}
		user.sendMessage(tl("requestSent", player.getDisplayName()));
	}
}
