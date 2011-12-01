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

	//Todo Translate
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

	private String getLocal(final Server server, final User user, final long radius)
	{
		final Location loc = user.getLocation();
		final World world = loc.getWorld();
		final int x = loc.getBlockX();
		final int y = loc.getBlockY();
		final int z = loc.getBlockZ();
		final StringBuilder output = new StringBuilder();

		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final User player = ess.getUser(onlinePlayer);
			if (!player.equals(user) && !player.isHidden())
			{
				final Location l = player.getLocation();
				final int dx = x - l.getBlockX();
				final int dy = y - l.getBlockY();
				final int dz = z - l.getBlockZ();
				final long delta = dx * dx + dy * dy + dz * dz;
				if (delta > radius || world != l.getWorld())
				{
					if (output.length() > 0)
					{
						output.append(", ");
					}
					output.append(user.getDisplayName()).append("&f(&4").append(delta).append("m&f)");
				}
			}
		}
		return output.length() > 1 ? output.toString() : _("none");
	}
}
