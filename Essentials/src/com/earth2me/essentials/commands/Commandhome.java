package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.List;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandhome extends EssentialsCommand
{
	public Commandhome()
	{
		super("home");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		User player = user;
		String homeName = "";
		String[] nameParts;
		if (args.length > 0)
		{
			nameParts = args[0].split(":");
			if (nameParts[0].length() == args[0].length() || !user.isAuthorized("essentials.home.others"))
			{
				homeName = nameParts[0];
			}
			else
			{
				player = getPlayer(server, nameParts, 0, true);
				if (nameParts.length > 1)
				{
					homeName = nameParts[1];
				}
			}
		}
		try
		{
			if ("bed".equalsIgnoreCase(homeName))
			{
				final Location bed = player.getBedSpawnLocation();
				if (bed != null)
				{
					user.getTeleport().teleport(bed, charge, TeleportCause.COMMAND);
					throw new NoChargeException();
				}
			}
			user.getTeleport().home(player, homeName.toLowerCase(Locale.ENGLISH), charge);
		}
		catch (NotEnoughArgumentsException e)
		{
			final List<String> homes = player.getHomes();
			if (homes.isEmpty() && player.equals(user))
			{
				final Location bed = player.getBedSpawnLocation();
				if (bed != null && bed.getBlock().getType() == Material.BED_BLOCK)
				{
					user.getTeleport().teleport(bed, charge, TeleportCause.COMMAND);
					throw new NoChargeException();
				}
				user.getTeleport().respawn(charge, TeleportCause.COMMAND);

			}
			else if (homes.isEmpty())
			{
				throw new Exception(player == user ? _("noHomeSet") : _("noHomeSetPlayer"));
			}
			else if (homes.size() == 1 && player.equals(user))
			{
				user.getTeleport().home(player, homes.get(0), charge);
			}
			else
			{
				homes.add("bed");
				user.sendMessage(_("homes", Util.joinList(homes)));
			}
		}
		throw new NoChargeException();
	}
}
