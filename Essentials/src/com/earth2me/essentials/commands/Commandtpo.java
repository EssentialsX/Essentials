package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandtpo extends EssentialsCommand
{
	public Commandtpo()
	{
		super("tpo");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		switch (args.length)
		{
		case 0:
			throw new NotEnoughArgumentsException();

		case 1:
			final User player = getPlayer(server, user, args, 0);
			if (user.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions()
				&& !user.isAuthorized("essentials.worlds." + player.getWorld().getName()))
			{
				throw new Exception(tl("noPerm", "essentials.worlds." + player.getWorld().getName()));
			}
			user.getTeleport().now(player.getBase(), false, TeleportCause.COMMAND);
			break;

		default:
			if (!user.isAuthorized("essentials.tp.others"))
			{
				throw new Exception(tl("noPerm", "essentials.tp.others"));
			}
			final User target = getPlayer(server, user, args, 0);
			final User toPlayer = getPlayer(server, user, args, 1);

			if (target.getWorld() != toPlayer.getWorld() && ess.getSettings().isWorldTeleportPermissions()
				&& !user.isAuthorized("essentials.worlds." + toPlayer.getWorld().getName()))
			{
				throw new Exception(tl("noPerm", "essentials.worlds." + toPlayer.getWorld().getName()));
			}

			target.getTeleport().now(toPlayer.getBase(), false, TeleportCause.COMMAND);
			target.sendMessage(tl("teleportAtoB", user.getDisplayName(), toPlayer.getDisplayName()));
			break;
		}
	}
}
