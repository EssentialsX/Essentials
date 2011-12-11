package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.user.UserData.TimestampType;
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
			IUser u = getPlayer(server, args, 0);
			sender.sendMessage(_("seenOnline", u.getDisplayName(), Util.formatDateDiff(u.getTimestamp(TimestampType.LOGIN))));
		}
		catch (NoSuchFieldException e)
		{
			IUser u = ess.getOfflineUser(args[0]);
			if (u == null)
			{
				throw new Exception(_("playerNotFound"));
			}
			sender.sendMessage(_("seenOffline", u.getDisplayName(), Util.formatDateDiff(u.getTimestamp(TimestampType.LOGOUT))));
			if (u.isBanned())
			{
				sender.sendMessage(_("whoisBanned", _("true")));
			}
		}
	}
}
