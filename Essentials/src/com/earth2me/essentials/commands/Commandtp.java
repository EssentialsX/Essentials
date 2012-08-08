package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandtp extends EssentialsCommand
{
	public Commandtp()
	{
		super("tp");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		switch (args.length)
		{
		case 0:
			throw new NotEnoughArgumentsException();

		case 1:
			final User player = getPlayer(server, args, 0);
			if (!player.isTeleportEnabled())
			{
				throw new Exception(_("teleportDisabled", player.getDisplayName()));
			}
			if (user.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions()
				&& !user.isAuthorized("essentials.world." + player.getWorld().getName()))
			{
				throw new Exception(_("noPerm", "essentials.world." + player.getWorld().getName()));
			}
			user.sendMessage(_("teleporting"));
			final Trade charge = new Trade(this.getName(), ess);
			charge.isAffordableFor(user);
			user.getTeleport().teleport(player, charge, TeleportCause.COMMAND);
			throw new NoChargeException();

		default:
			if (!user.isAuthorized("essentials.tp.others"))
			{
				throw new Exception(_("noPerm", "essentials.tp.others"));
			}
			user.sendMessage(_("teleporting"));
			final User target = getPlayer(server, args, 0);
			final User toPlayer = getPlayer(server, args, 1);
			if (!target.isTeleportEnabled())
			{
				throw new Exception(_("teleportDisabled", target.getDisplayName()));
			}
			if (!toPlayer.isTeleportEnabled())
			{
				throw new Exception(_("teleportDisabled", toPlayer.getDisplayName()));
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

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		sender.sendMessage(_("teleporting"));
		final User target = getPlayer(server, args, 0);
		final User toPlayer = getPlayer(server, args, 1);
		target.getTeleport().now(toPlayer, false, TeleportCause.COMMAND);
		target.sendMessage(_("teleportAtoB", Console.NAME, toPlayer.getDisplayName()));
	}
}
