package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandnear extends EssentialsCommand
{
	@Override
	protected void run(final IUser user, final String[] args) throws Exception
	{
		long radius = 200;
		IUser otherUser = null;

		if (args.length > 0)
		{
			try
			{
				otherUser = getPlayer(args, 0);
			}
			catch (Exception ex)
			{
				try
				{
					radius = Long.parseLong(args[0]);
				}
				catch (NumberFormatException e)
				{
				}
			}
		}
		if (args.length > 1 && otherUser != null)
		{
			try
			{
				radius = Long.parseLong(args[1]);
			}
			catch (NumberFormatException e)
			{
			}
		}
		if (otherUser == null || user.isAuthorized("essentials.near.others"))
		{
			user.sendMessage(_("nearbyPlayers", getLocal(otherUser == null ? user : otherUser, radius)));
		}
		else
		{
			user.sendMessage(_("noAccessCommand"));
		}
	}

	@Override
	protected void run(final CommandSender sender, final String[] args) throws Exception
	{

		IUser otherUser = null;
		if (args.length > 0)
		{
			otherUser = getPlayer(args, 0);
		}
		else
		{
			throw new NotEnoughArgumentsException();
		}
		long radius = 200;
		if (args.length > 1)
		{
			try
			{
				radius = Long.parseLong(args[1]);
			}
			catch (NumberFormatException e)
			{
			}
		}
		sender.sendMessage(_("nearbyPlayers", getLocal(otherUser, radius)));
	}

	private String getLocal(final IUser user, final long radius)
	{
		final Location loc = user.getLocation();
		final World world = loc.getWorld();
		final StringBuilder output = new StringBuilder();
		final long radiusSquared = radius * radius;

		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final IUser player = ess.getUser(onlinePlayer);
			if (!player.equals(user) && !player.isHidden())
			{
				final Location playerLoc = player.getLocation();
				if (playerLoc.getWorld() != world)
				{
					continue;
				}

				final long delta = (long)playerLoc.distanceSquared(loc);
				if (delta < radiusSquared)
				{
					if (output.length() > 0)
					{
						output.append(", ");
					}
					output.append(player.getDisplayName()).append("§f(§4").append((long)Math.sqrt(delta)).append("m§f)");
				}
			}
		}
		return output.length() > 1 ? output.toString() : _("none");
	}
}
