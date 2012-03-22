package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandping extends EssentialsCommand
{
	public Commandping()
	{
		super("ping");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{

			sender.sendMessage(_("pong"));
		}
		else
		{
			sender.sendMessage(Util.replaceFormat(getFinalArg(args, 0)));
		}
	}
}
