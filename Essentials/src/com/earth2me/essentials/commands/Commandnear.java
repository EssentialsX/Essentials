package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class Commandnear extends EssentialsCommand
{
	public Commandnear()
	{
		super("near");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		long maxRadius = ess.getSettings().getChatRadius();
		
		if (maxRadius == 0)
		{
			maxRadius = 200;
		}
		
		long radius = maxRadius;
		
		User otherUser = null;

		if (args.length > 0)
		{
			try
			{
				radius = Long.parseLong(args[0]);
			}
			catch (NumberFormatException e)
			{
				try
				{
					otherUser = getPlayer(server, user, args, 0);
				}
				catch (Exception ex)
				{
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
		}
		
		radius = Math.abs(radius);
		
		if (radius > maxRadius && !user.isAuthorized("essentials.near.maxexempt"))
		{
			user.sendMessage(tl("radiusTooBig", maxRadius));
			radius = maxRadius;
		}
		
		if (otherUser == null || !user.isAuthorized("essentials.near.others"))
		{
			otherUser = user;
		}
		user.sendMessage(tl("nearbyPlayers", getLocal(server, otherUser, radius)));
	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length == 0)
		{
			throw new NotEnoughArgumentsException();
		}
		final User otherUser = getPlayer(server, args, 0, true, false);
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
		sender.sendMessage(tl("nearbyPlayers", getLocal(server, otherUser, radius)));
	}

	private String getLocal(final Server server, final User user, final long radius)
	{
		final Location loc = user.getLocation();
		final World world = loc.getWorld();
		final StringBuilder output = new StringBuilder();
		final long radiusSquared = radius * radius;
		boolean showHidden = user.canInteractVanished();

		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final User player = ess.getUser(onlinePlayer);
			if (!player.equals(user) && (!player.isHidden(user.getBase()) || showHidden || user.getBase().canSee(onlinePlayer)))
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
		return output.length() > 1 ? output.toString() : tl("none");
	}
}
