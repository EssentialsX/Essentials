package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import java.util.Locale;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandwhois extends EssentialsCommand
{
	public Commandwhois()
	{
		super("whois");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		boolean showhidden = false;
		if (sender instanceof Player)
		{
			if (ess.getUser(sender).isAuthorized("essentials.list.hidden"))
			{
				showhidden = true;
			}
		}
		else
		{
			showhidden = true;
		}
		final String whois = args[0].toLowerCase(Locale.ENGLISH);
		final int prefixLength = Util.stripColor(ess.getSettings().getNicknamePrefix()).length();
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final User user = ess.getUser(onlinePlayer);
			if (user.isHidden() && !showhidden)
			{
				continue;
			}
			final String nickName = Util.stripColor(user.getNickname());
			if (!whois.equalsIgnoreCase(nickName)
				&& !whois.substring(prefixLength).equalsIgnoreCase(nickName)
				&& !whois.equalsIgnoreCase(user.getName()))
			{
				continue;
			}
			sender.sendMessage("");
			user.setDisplayNick();
			sender.sendMessage(_("whoisIs", user.getDisplayName(), user.getName()));
			sender.sendMessage(_("whoisHealth", user.getHealth()));
			sender.sendMessage(_("whoisExp", SetExpFix.getTotalExperience(user), user.getLevel()));
			sender.sendMessage(_("whoisOP", (user.isOp() ? _("true") : _("false"))));
			sender.sendMessage(_("whoisGod", (user.isGodModeEnabled() ? _("true") : _("false"))));
			sender.sendMessage(_("whoisGamemode", _(user.getGameMode().toString().toLowerCase(Locale.ENGLISH))));
			sender.sendMessage(_("whoisLocation", user.getLocation().getWorld().getName(), user.getLocation().getBlockX(), user.getLocation().getBlockY(), user.getLocation().getBlockZ()));
			if (!ess.getSettings().isEcoDisabled())
			{
				sender.sendMessage(_("whoisMoney", Util.displayCurrency(user.getMoney(), ess)));
			}
			sender.sendMessage(_("whoisJail", (user.isJailed()
											   ? user.getJailTimeout() > 0
												 ? Util.formatDateDiff(user.getJailTimeout())
												 : _("true")
											   : _("false"))));
			sender.sendMessage(user.isAfk()
							   ? _("whoisStatusAway")
							   : _("whoisStatusAvailable"));
			sender.sendMessage(_("whoisIPAddress", user.getAddress().getAddress().toString()));
			final String location = user.getGeoLocation();
			if (location != null
				&& (sender instanceof Player ? ess.getUser(sender).isAuthorized("essentials.geoip.show") : true))
			{
				sender.sendMessage(_("whoisGeoLocation", location));
			}
		}
	}
}
