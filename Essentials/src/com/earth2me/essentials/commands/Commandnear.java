package com.earth2me.essentials.commands;


import static com.earth2me.essentials.I18n._;
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
		long radius = 100;
		if (args.length > 0)
		{
			try
			{
				radius = Long.parseLong(args[0]);
			}
			catch (NumberFormatException e)
			{
			}
		}
		user.sendMessage(_("nearbyPlayers", getLocal(server, user, radius)));
	}

	private String getLocal(final Server server, final User user, long radius)
	{
		final Location loc = user.getLocation();
		final World world = loc.getWorld();		
		final StringBuilder output = new StringBuilder();
		radius *= radius;

		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final User player = ess.getUser(onlinePlayer);
			if (!player.equals(user) && !player.isHidden())
			{
				final Location playerLoc = player.getLocation();
				if (playerLoc.getWorld() != world) { continue; }
				
				final long delta = (long)playerLoc.distanceSquared(loc);				
				if (delta < radius)
				{
					if (output.length() > 0)
					{
						output.append(", ");
					}
					output.append(player.getDisplayName()).append("§f(§4").append(Math.sqrt(delta)).append("m§f)");
				}
			}
		}
		return output.length() > 1 ? output.toString() : _("none");
	}
}
