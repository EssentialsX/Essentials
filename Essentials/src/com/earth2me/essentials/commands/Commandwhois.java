package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import java.util.Locale;
import lombok.Cleanup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandwhois extends EssentialsCommand
{
	@Override
	public void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		boolean showhidden = false;
		if (sender instanceof Player)
		{
			if (ess.getUser((Player)sender).isAuthorized("essentials.list.hidden"))
			{
				showhidden = true;
			}
		}
		else
		{
			showhidden = true;
		}
		final String whois = args[0].toLowerCase(Locale.ENGLISH);
		@Cleanup
		ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		final int prefixLength = Util.stripColor(settings.getData().getChat().getNicknamePrefix()).length();
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			@Cleanup
			final IUser user = ess.getUser(onlinePlayer);

			if (user.isHidden() && !showhidden)
			{
				continue;
			}
			user.acquireReadLock();
			final String nickName = Util.stripColor(user.getData().getNickname());
			if (!whois.equalsIgnoreCase(nickName)
				&& !whois.substring(prefixLength).equalsIgnoreCase(nickName)
				&& !whois.equalsIgnoreCase(user.getName()))
			{
				continue;
			}
			sender.sendMessage("");
			sender.sendMessage(_("whoisIs", user.getDisplayName(), user.getName()));
			sender.sendMessage(_("whoisHealth", user.getHealth()));
			sender.sendMessage(_("whoisOP", (user.isOp() ? _("true") : _("false"))));
			sender.sendMessage(_("whoisGod", (user.isGodModeEnabled() ? _("true") : _("false"))));
			sender.sendMessage(_("whoisGamemode", _(user.getGameMode().toString().toLowerCase(Locale.ENGLISH))));
			sender.sendMessage(_("whoisLocation", user.getLocation().getWorld().getName(), user.getLocation().getBlockX(), user.getLocation().getBlockY(), user.getLocation().getBlockZ()));
			sender.sendMessage(_("whoisMoney", Util.formatCurrency(user.getMoney(), ess)));
			sender.sendMessage(user.getData().isAfk()
							   ? _("whoisStatusAway")
							   : _("whoisStatusAvailable"));
			sender.sendMessage(_("whoisIPAddress", user.getAddress().getAddress().toString()));
			final String location = user.getData().getGeolocation();
			if (location != null
				&& (sender instanceof Player ? ess.getUser((Player)sender).isAuthorized("essentials.geoip.show") : true))
			{
				sender.sendMessage(_("whoisGeoLocation", location));
			}
		}
	}
}
