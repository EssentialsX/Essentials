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
		seen(server, sender, args, true);
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		seen(server, user, args, user.isAuthorized("essentials.seen.banreason"));
	}

	protected void seen(final Server server, final CommandSender sender, final String[] args, final boolean show) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		try
		{
			User player = getPlayer(server, args, 0);
			player.setDisplayNick();
			sender.sendMessage(_("seenOnline", player.getDisplayName(), Util.formatDateDiff(player.getLastLogin())));
		}
		catch (NoSuchFieldException e)
		{
			User player = ess.getOfflineUser(args[0]);
			if (player == null)
			{
				throw new Exception(_("playerNotFound"));
			}
			player.setDisplayNick();
			sender.sendMessage(_("seenOffline", player.getDisplayName(), Util.formatDateDiff(player.getLastLogout())));
			if (player.isBanned())
			{
				sender.sendMessage(_("whoisBanned", show ? player.getBanReason() : _("true")));
			}
		}
	}
}
