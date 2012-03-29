package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
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
			final User player = getPlayer(server, args, 0, true);
			if (!player.isOnline() || (player.isHidden() && !user.isAuthorized("essentials.teleport.hidden")))
			{
				throw new NoSuchFieldException(_("playerNotFound"));
			}
			if (user.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions()
				&& !user.isAuthorized("essentials.world." + player.getWorld().getName()))
			{
				throw new Exception(_("noPerm", "essentials.world." + player.getWorld().getName()));
			}
			user.sendMessage(_("teleporting"));
			user.getTeleport().now(player, false, TeleportCause.COMMAND);
			break;

		default:
			if (!user.isAuthorized("essentials.tp.others"))
			{
				throw new Exception(_("noPerm", "essentials.tp.others"));
			}
			user.sendMessage(_("teleporting"));
			final User target = getPlayer(server, args, 0, true);
			final User toPlayer = getPlayer(server, args, 1, true);

			if (!target.isOnline() || !toPlayer.isOnline()
				|| ((target.isHidden() || toPlayer.isHidden()) && !user.isAuthorized("essentials.teleport.hidden")))
			{
				throw new NoSuchFieldException(_("playerNotFound"));
			}

			if (target.getWorld() != toPlayer.getWorld() && ess.getSettings().isWorldTeleportPermissions()
				&& !user.isAuthorized("essentials.world." + toPlayer.getWorld().getName()))
			{
				throw new Exception(_("noPerm", "essentials.world." + toPlayer.getWorld().getName()));
			}

			target.getTeleport().now(toPlayer, false, TeleportCause.COMMAND);
			target.sendMessage(_("teleportAtoB", user.getDisplayName(), toPlayer.getDisplayName()));
			break;
		}
	}
}
