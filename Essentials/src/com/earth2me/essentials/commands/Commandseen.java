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
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		try
		{
			User u = getPlayer(server, args, 0);
			sender.sendMessage(_("seenOnline", u.getDisplayName(), Util.formatDateDiff(u.getLastLogin())));
		}
		catch (NoSuchFieldException e)
		{
			User u = ess.getOfflineUser(args[0]);
			if (u == null)
			{
				throw new Exception(_("playerNotFound"));
			}
			sender.sendMessage(_("seenOffline", u.getDisplayName(), Util.formatDateDiff(u.getLastLogout())));
		}
	}
}
