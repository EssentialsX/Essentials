package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Location;
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
				&& !user.isAuthorized("essentials.worlds." + player.getWorld().getName()))
			{
				throw new Exception(_("noPerm", "essentials.worlds." + player.getWorld().getName()));
			}
			user.sendMessage(_("teleporting"));
			final Trade charge = new Trade(this.getName(), ess);
			charge.isAffordableFor(user);
			user.getTeleport().teleport(player, charge, TeleportCause.COMMAND);
			throw new NoChargeException();
		case 4:
			if (!user.isAuthorized("essentials.tp.others"))
			{
				throw new Exception(_("noPerm", "essentials.tp.others"));
			}
			user.sendMessage(_("teleporting"));
			final User target2 = getPlayer(server, args, 0);
			final double x = args[1].startsWith("~") ? target2.getLocation().getX() + Integer.parseInt(args[1].substring(1)) : Integer.parseInt(args[1]);
			final double y = args[2].startsWith("~") ? target2.getLocation().getY() + Integer.parseInt(args[2].substring(1)) : Integer.parseInt(args[2]);
			final double z = args[3].startsWith("~") ? target2.getLocation().getZ() + Integer.parseInt(args[3].substring(1)) : Integer.parseInt(args[3]);
			if (x > 30000000 || y > 30000000 || z > 30000000 || x < -30000000 || y < -30000000 || z < -30000000)
			{
				throw new NotEnoughArgumentsException("Value of coordinates cannot be over 30000000"); //todo: I18n
			}
			final Location location = new Location(target2.getWorld(), x, y, z);
			if (!target2.isTeleportEnabled())
			{
				throw new Exception(_("teleportDisabled", target2.getDisplayName()));
			}
			target2.getTeleport().now(location, false, TeleportCause.COMMAND);
			target2.sendMessage(_("teleporting"));
		case 2:
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
				&& !user.isAuthorized("essentials.worlds." + toPlayer.getWorld().getName()))
			{
				throw new Exception(_("noPerm", "essentials.worlds." + toPlayer.getWorld().getName()));
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

		final User target = getPlayer(server, args, 0);
		if (args.length == 2)
		{
			final User toPlayer = getPlayer(server, args, 1);
			target.getTeleport().now(toPlayer, false, TeleportCause.COMMAND);
			target.sendMessage(_("teleportAtoB", Console.NAME, toPlayer.getDisplayName()));
		}
		else if (args.length > 3)
		{
			final double x = args[1].startsWith("~") ? target.getLocation().getX() + Integer.parseInt(args[1].substring(1)) : Integer.parseInt(args[1]);
			final double y = args[2].startsWith("~") ? target.getLocation().getY() + Integer.parseInt(args[2].substring(1)) : Integer.parseInt(args[2]);
			final double z = args[3].startsWith("~") ? target.getLocation().getZ() + Integer.parseInt(args[3].substring(1)) : Integer.parseInt(args[3]);
			if (x > 30000000 || y > 30000000 || z > 30000000 || x < -30000000 || y < -30000000 || z < -30000000)
			{
				throw new NotEnoughArgumentsException("Value of coordinates cannot be over 30000000"); //todo: I18n
			}
			final Location location = new Location(target.getWorld(), x, y, z);
			target.getTeleport().now(location, false, TeleportCause.COMMAND);
			target.sendMessage(_("teleporting"));
		} else {
			throw new NotEnoughArgumentsException();
		}
		sender.sendMessage(_("teleporting"));
	}
}
