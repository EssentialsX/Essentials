package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandseen extends EssentialsCommand
{
	public Commandseen()
	{
		super("seen");
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		try
		{
			User user = getPlayer(server, args, 0);
			sender.sendMessage(_("seenOnline", user.getDisplayName(), Util.formatDateDiff(user.getLastLogin())));
		}
		catch (NoSuchFieldException e)
		{
			User user = ess.getOfflineUser(args[0]);
			if (user == null)
			{
				throw new Exception(_("playerNotFound"));
			}
			sender.sendMessage(_("seenOffline", user.getDisplayName(), Util.formatDateDiff(user.getLastLogout())));
			if (user.isBanned())
			{
				sender.sendMessage(_("whoisBanned", user.getBanReason()));
			}
		}
	}
}
