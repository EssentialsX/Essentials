package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import org.bukkit.command.CommandSender;


public class Commandbroadcast extends EssentialsCommand
{
	@Override
	public void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		ess.broadcastMessage(null, _("broadcast", getFinalArg(args, 0)));
	}
}
