package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandbanip extends EssentialsCommand
{
	public Commandbanip()
	{
		super("banip");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final User player = ess.getUser(args[0]);

		if (player == null)
		{
			ess.getServer().banIP(args[0]);
			sender.sendMessage(_("banIpAddress"));
		}
		else
		{
			final String ipAddress = player.getLastLoginAddress();
			if (ipAddress.length() == 0)
			{
				throw new Exception(_("playerNotFound"));
			}
			ess.getServer().banIP(player.getLastLoginAddress());
			sender.sendMessage(_("banIpAddress"));
		}
	}
}
