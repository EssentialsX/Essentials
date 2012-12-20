package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Location;
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
		seen(server, sender, args, true, true);
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		seen(server, user, args, user.isAuthorized("essentials.seen.banreason"), user.isAuthorized("essentials.seen.extra"));
	}

	protected void seen(final Server server, final CommandSender sender, final String[] args, final boolean showBan, final boolean extra) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		try
		{
			User user = getPlayer(server, args, 0);
			user.setDisplayNick();
			sender.sendMessage(_("seenOnline", user.getDisplayName(), Util.formatDateDiff(user.getLastLogin())));
			sender.sendMessage(_("whoisAFK", (user.isAfk() ? _("true") : _("false"))));
			sender.sendMessage(_("whoisJail", (user.isJailed()
											   ? user.getJailTimeout() > 0
												 ? Util.formatDateDiff(user.getJailTimeout())
												 : _("true")
											   : _("false"))));
			sender.sendMessage(_("whoisMuted", (user.isMuted()
												? user.getMuteTimeout() > 0
												  ? Util.formatDateDiff(user.getMuteTimeout())
												  : _("true")
												: _("false"))));
			if (extra)
			{
				sender.sendMessage(_("whoisIPAddress", user.getAddress().getAddress().toString()));
			}
		}
		catch (NoSuchFieldException e)
		{
			User player = ess.getOfflineUser(args[0]);
			if (player == null)
			{
				throw new Exception(_("playerNotFound"));
			}
			player.setDisplayNick();
			sender.sendMessage(_("seenOffline", player.getName(), Util.formatDateDiff(player.getLastLogout())));
			if (player.isBanned())
			{
				sender.sendMessage(_("whoisBanned", showBan ? player.getBanReason() : _("true")));
			}
			if (extra)
			{
				sender.sendMessage(_("whoisIPAddress", player.getLastLoginAddress()));
				final Location loc = player.getLastLocation();
				if (loc != null)
				{
					sender.sendMessage(_("whoisLocation", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
				}
			}
		}
	}
}
