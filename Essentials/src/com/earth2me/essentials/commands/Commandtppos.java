package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandtppos extends EssentialsCommand
{
	public Commandtppos()
	{
		super("tppos");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 3)
		{
			throw new NotEnoughArgumentsException();
		}

		final int x = Integer.parseInt(args[0]);
		final int y = Integer.parseInt(args[1]);
		final int z = Integer.parseInt(args[2]);
		final Location location = new Location(user.getWorld(), x, y, z);
		if (args.length > 3)
		{
			location.setYaw((Float.parseFloat(args[3]) + 180 + 360) % 360);
		}
		if (args.length > 4)
		{
			location.setPitch(Float.parseFloat(args[4]));
		}
		final Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		user.sendMessage(_("teleporting"));
		user.getTeleport().teleport(location, charge, TeleportCause.COMMAND);
		throw new NoChargeException();
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 4)
		{
			throw new NotEnoughArgumentsException();
		}

		User user = ess.getUser(server.getPlayer(args[0]));
		final int x = Integer.parseInt(args[1]);
		final int y = Integer.parseInt(args[2]);
		final int z = Integer.parseInt(args[3]);
		final Location location = new Location(user.getWorld(), x, y, z);
		if (args.length > 4)
		{
			location.setYaw((Float.parseFloat(args[4]) + 180 + 360) % 360);
		}
		if (args.length > 5)
		{
			location.setPitch(Float.parseFloat(args[5]));
		}
		sender.sendMessage(_("teleporting"));
		user.sendMessage(_("teleporting"));
		user.getTeleport().teleport(location, null, TeleportCause.COMMAND);
	}
}