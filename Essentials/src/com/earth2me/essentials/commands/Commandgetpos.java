package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;


public class Commandgetpos extends EssentialsCommand
{
	public Commandgetpos()
	{
		super("getpos");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0 && user.isAuthorized("essentials.getpos.others"))
		{
			final User otherUser = getPlayer(server, user, args, 0);
			outputPosition(user.getSource(), otherUser.getLocation(), user.getLocation());
			return;
		}
		outputPosition(user.getSource(), user.getLocation(), null);
	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final User user = getPlayer(server, args, 0, true, false);
		outputPosition(sender, user.getLocation(), null);
	}

	private void outputPosition(final CommandSource sender, final Location coords, final Location distance)
	{
		sender.sendMessage(_("currentWorld", coords.getWorld().getName()));
		sender.sendMessage(_("posX", coords.getBlockX()));
		sender.sendMessage(_("posY", coords.getBlockY()));
		sender.sendMessage(_("posZ", coords.getBlockZ()));
		sender.sendMessage(_("posYaw", (coords.getYaw() + 180 + 360) % 360));
		sender.sendMessage(_("posPitch", coords.getPitch()));
		if (distance != null && coords.getWorld().equals(distance.getWorld()))
		{
			sender.sendMessage(_("distance", coords.distance(distance)));
		}
	}
}
