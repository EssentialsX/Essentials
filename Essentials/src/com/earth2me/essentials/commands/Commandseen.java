package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import com.earth2me.essentials.Util;
import java.util.ArrayList;
import java.util.List;
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
		seen(server, sender, args, true, true, true);
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		seen(server, user, args, user.isAuthorized("essentials.seen.banreason"), user.isAuthorized("essentials.seen.extra"), user.isAuthorized("essentials.seen.ipsearch"));
	}

	protected void seen(final Server server, final CommandSender sender, final String[] args, final boolean showBan, final boolean extra, final boolean ipLookup) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		try
		{
			User user = getPlayer(server, args, 0);
			seenOnline(server, sender, user, showBan, extra);
		}
		catch (NoSuchFieldException e)
		{
			User player = ess.getOfflineUser(args[0]);
			if (player == null)
			{
				if (ipLookup && Util.validIP(args[0]))
				{
					seenIP(server, sender, args[0]);
					return;
				}
				else
				{
					throw new Exception(_("playerNotFound"));
				}
			}
			seenOffline(server, sender, player, showBan, extra);
		}
	}

	private void seenOnline(final Server server, final CommandSender sender, final User user, final boolean showBan, final boolean extra) throws Exception
	{

		user.setDisplayNick();
		sender.sendMessage(_("seenOnline", user.getDisplayName(), Util.formatDateDiff(user.getLastLogin())));
		if (user.isAfk())
		{
			sender.sendMessage(_("whoisAFK", _("true")));
		}
		if (user.isJailed())
		{
			sender.sendMessage(_("whoisJail", (user.getJailTimeout() > 0
											   ? Util.formatDateDiff(user.getJailTimeout())
											   : _("true"))));
		}
		if (user.isMuted())
		{
			sender.sendMessage(_("whoisMuted", (user.getMuteTimeout() > 0
												? Util.formatDateDiff(user.getMuteTimeout())
												: _("true"))));
		}
		if (extra)
		{
			sender.sendMessage(_("whoisIPAddress", user.getAddress().getAddress().toString()));
		}
	}

	private void seenOffline(final Server server, final CommandSender sender, User player, final boolean showBan, final boolean extra) throws Exception
	{
		player.setDisplayNick();
		if (player.getLastLogout() > 0)
		{
			sender.sendMessage(_("seenOffline", player.getName(), Util.formatDateDiff(player.getLastLogout())));
		}
		else
		{
			sender.sendMessage(_("userUnknown", player.getName()));
		}
		if (player.isBanned())
		{
			sender.sendMessage(_("whoisBanned", showBan ? player.getBanReason() : _("true")));
		}
		if (extra)
		{
			if (!player.getLastLoginAddress().isEmpty())
			{
				sender.sendMessage(_("whoisIPAddress", player.getLastLoginAddress()));
			}
			final Location loc = player.getLogoutLocation();
			if (loc != null)
			{
				sender.sendMessage(_("whoisLocation", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			}
		}
	}

	private void seenIP(final Server server, final CommandSender sender, final String ipAddress) throws Exception
	{
		final UserMap userMap = ess.getUserMap();
		sender.sendMessage(_("runningPlayerMatch", ipAddress));

		ess.runTaskAsynchronously(new Runnable()
		{
			@Override
			public void run()
			{
				final List<String> matches = new ArrayList<String>();
				for (final String u : userMap.getAllUniqueUsers())
				{
					final User user = ess.getUserMap().getUser(u);
					if (user == null)
					{
						continue;
					}

					final String uIPAddress = user.getLastLoginAddress();

					if (!uIPAddress.isEmpty() && uIPAddress.equalsIgnoreCase(ipAddress))
					{
						matches.add(user.getName());
					}
				}

				if (matches.size() > 0)
				{
					sender.sendMessage(_("matchingIPAddress"));
					sender.sendMessage(Util.joinList(matches));
				}
				else
				{
					sender.sendMessage(_("noMatchingPlayers"));
				}

			}
		});

	}
}
