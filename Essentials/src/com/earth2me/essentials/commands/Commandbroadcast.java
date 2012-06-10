package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandbroadcast extends EssentialsCommand
{
	public Commandbroadcast()
	{
		super("broadcast");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		ess.broadcastMessage(null, _("broadcast", Util.replaceFormat(getFinalArg(args, 0))));
	}
}
