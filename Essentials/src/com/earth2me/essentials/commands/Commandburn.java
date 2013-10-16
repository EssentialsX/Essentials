package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandburn extends EssentialsCommand
{
	public Commandburn()
	{
		super("burn");
	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		if (args[0].trim().length() < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		User user = getPlayer(server, sender, args, 0);
		user.setFireTicks(Integer.parseInt(args[1]) * 20);
		sender.sendMessage(_("burnMsg", user.getDisplayName(), Integer.parseInt(args[1])));
	}
}
