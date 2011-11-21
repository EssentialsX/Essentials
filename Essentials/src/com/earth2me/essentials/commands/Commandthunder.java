package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.World;


public class Commandthunder extends EssentialsCommand
{
	public Commandthunder()
	{
		super("thunder");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final World world = user.getWorld();
		final boolean setThunder = args[0].equalsIgnoreCase("true");
		if (args.length > 1)
		{

			world.setThundering(setThunder ? true : false);
			world.setThunderDuration(Integer.parseInt(args[1]) * 20);
			user.sendMessage(_("thunderDuration", (setThunder ? _("enabled") : _("disabled")), Integer.parseInt(args[1])));

		}
		else
		{
			world.setThundering(setThunder ? true : false);
			user.sendMessage(_("thunder", setThunder ? _("enabled") : _("disabled")));
		}

	}
}
