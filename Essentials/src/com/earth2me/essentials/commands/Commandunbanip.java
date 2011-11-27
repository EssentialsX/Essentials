package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandunbanip extends EssentialsCommand
{
	public Commandunbanip()
	{
		super("unbanip");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		try
		{
			final User user = getPlayer(server, args, 0, true);
			ess.getServer().unbanIP(user.getLastLoginAddress());
		}
		catch (Exception ex)
		{
		}
		ess.getServer().unbanIP(args[0]);
		sender.sendMessage(_("unbannedIP"));
	}
}
