package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import com.earth2me.essentials.craftbukkit.BanLookup;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.StringUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bukkit.BanList;
import java.util.UUID;
import org.bukkit.BanEntry;
import org.bukkit.Location;
import org.bukkit.Server;


public class Commandseen extends EssentialsCommand
{
	public Commandseen()
	{
		super("seen");
	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		seen(server, sender, args, true, true, true);
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		seen(server, user.getSource(), args, user.isAuthorized("essentials.seen.banreason"), user.isAuthorized("essentials.seen.extra"), user.isAuthorized("essentials.seen.ipsearch"));
	}

	protected void seen(final Server server, final CommandSource sender, final String[] args, final boolean showBan, final boolean extra, final boolean ipLookup) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		User player = ess.getOfflineUser(args[0]);
		if (player == null)
		{
			if (ipLookup && FormatUtil.validIP(args[0]))
			{
				seenIP(server, sender, args[0]);
				return;
			}
			else if (ess.getServer().getBanList(BanList.Type.IP).isBanned(args[0]))
			{
				sender.sendMessage(tl("isIpBanned", args[0]));
				return;
			}
			else if (BanLookup.isBanned(ess, args[0]))
			{
				sender.sendMessage(tl("whoisBanned", showBan ? BanLookup.getBanEntry(ess, args[0]).getReason() : tl("true")));
				return;
			}
			else
			{
				try
				{
					player = getPlayer(server, sender, args, 0);
				}
				catch (NoSuchFieldException e)
				{
					throw new PlayerNotFoundException();
				}
			}
		}
		if (player.getBase().isOnline() && canInteractWith(sender, player))
		{
			seenOnline(server, sender, player, showBan, extra);
		}
		else
		{
			seenOffline(server, sender, player, showBan, extra);
		}

	}

	private void seenOnline(final Server server, final CommandSource sender, final User user, final boolean showBan, final boolean extra) throws Exception
	{

		user.setDisplayNick();
		sender.sendMessage(tl("seenOnline", user.getDisplayName(), DateUtil.formatDateDiff(user.getLastLogin())));

		if (ess.getSettings().isDebug())
		{
			ess.getLogger().info("UUID: " + user.getBase().getUniqueId().toString());
		}

		List<String> history = ess.getUserMap().getUserHistory(user.getBase().getUniqueId());
		if (history != null && history.size() > 1)
		{
			sender.sendMessage(tl("seenAccounts", StringUtil.joinListSkip(", ", user.getName(), history)));
		}

		if (user.isAfk())
		{
			sender.sendMessage(tl("whoisAFK", tl("true")));
		}
		if (user.isJailed())
		{
			sender.sendMessage(tl("whoisJail", (user.getJailTimeout() > 0
												? DateUtil.formatDateDiff(user.getJailTimeout())
												: tl("true"))));
		}
		if (user.isMuted())
		{
			sender.sendMessage(tl("whoisMuted", (user.getMuteTimeout() > 0
												 ? DateUtil.formatDateDiff(user.getMuteTimeout())
												 : tl("true"))));
		}
		final String location = user.getGeoLocation();
		if (location != null && (!(sender.isPlayer()) || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show")))
		{
			sender.sendMessage(tl("whoisGeoLocation", location));
		}
		if (extra)
		{
			sender.sendMessage(tl("whoisIPAddress", user.getBase().getAddress().getAddress().toString()));
		}
	}

	private void seenOffline(final Server server, final CommandSource sender, User user, final boolean showBan, final boolean extra) throws Exception
	{
		user.setDisplayNick();
		if (user.getLastLogout() > 0)
		{
			sender.sendMessage(tl("seenOffline", user.getName(), DateUtil.formatDateDiff(user.getLastLogout())));
		}
		else
		{
			sender.sendMessage(tl("userUnknown", user.getName()));
		}

		if (ess.getSettings().isDebug())
		{
			ess.getLogger().info("UUID: " + user.getBase().getUniqueId().toString());
		}

		List<String> history = ess.getUserMap().getUserHistory(user.getBase().getUniqueId());
		if (history != null && history.size() > 1)
		{
			sender.sendMessage(tl("seenAccounts", StringUtil.joinListSkip(", ", user.getName(), history)));
		}

		if (BanLookup.isBanned(ess, user))
		{
			final BanEntry banEntry = BanLookup.getBanEntry(ess, user.getName());
			final String reason = showBan ? banEntry.getReason() : tl("true");
			sender.sendMessage(tl("whoisBanned", reason));
			if (banEntry.getExpiration() != null)
			{
				Date expiry = banEntry.getExpiration();
				String expireString = tl("now");
				if (expiry.after(new Date()))
				{
					expireString = DateUtil.formatDateDiff(expiry.getTime());
				}
				sender.sendMessage(tl("whoisTempBanned", expireString));
			}
		}

		final String location = user.getGeoLocation();
		if (location != null && (!(sender.isPlayer()) || ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show")))
		{
			sender.sendMessage(tl("whoisGeoLocation", location));
		}
		if (extra)
		{
			if (!user.getLastLoginAddress().isEmpty())
			{
				sender.sendMessage(tl("whoisIPAddress", user.getLastLoginAddress()));
			}
			final Location loc = user.getLogoutLocation();
			if (loc != null)
			{
				sender.sendMessage(tl("whoisLocation", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			}
		}
	}

	private void seenIP(final Server server, final CommandSource sender, final String ipAddress) throws Exception
	{
		final UserMap userMap = ess.getUserMap();

		if (ess.getServer().getBanList(BanList.Type.IP).isBanned(ipAddress))
		{
			sender.sendMessage(tl("isIpBanned", ipAddress));
		}

		sender.sendMessage(tl("runningPlayerMatch", ipAddress));

		ess.runTaskAsynchronously(new Runnable()
		{
			@Override
			public void run()
			{
				final List<String> matches = new ArrayList<String>();
				for (final UUID u : userMap.getAllUniqueUsers())
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
					sender.sendMessage(tl("matchingIPAddress"));
					sender.sendMessage(StringUtil.joinList(matches));
				}
				else
				{
					sender.sendMessage(tl("noMatchingPlayers"));
				}

			}
		});

	}
}
