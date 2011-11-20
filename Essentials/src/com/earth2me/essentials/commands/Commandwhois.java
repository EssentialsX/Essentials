package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.ChatColor;
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
		final String whois = args[0].toLowerCase();
		final int prefixLength = ChatColor.stripColor(ess.getSettings().getNicknamePrefix()).length();
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final User user = ess.getUser(onlinePlayer);
			if (user.isHidden() && !showhidden)
			{
				continue;
			}
			final String nickName = ChatColor.stripColor(user.getNickname());
			if (!whois.equalsIgnoreCase(nickName)
				&& !whois.substring(prefixLength).equalsIgnoreCase(nickName)
				&& !whois.equalsIgnoreCase(user.getName()))
			{
				continue;
			}
			sender.sendMessage("");
			sender.sendMessage(Util.format("whoisIs", user.getDisplayName(), user.getName()));
			sender.sendMessage(Util.format("whoisHealth", user.getHealth()));
			sender.sendMessage(Util.format("whoisOP", (user.isOp() ? Util.i18n("true") : Util.i18n("false"))));
			sender.sendMessage(Util.format("whoisGod", (user.isGodModeEnabled() ? Util.i18n("true") : Util.i18n("false"))));
			sender.sendMessage(Util.format("whoisGamemode", Util.i18n(user.getGameMode().toString().toLowerCase())));
			sender.sendMessage(Util.format("whoisLocation", user.getLocation().getWorld().getName(), user.getLocation().getBlockX(), user.getLocation().getBlockY(), user.getLocation().getBlockZ()));
			if (!ess.getSettings().isEcoDisabled())
			{
				sender.sendMessage(Util.format("whoisMoney", Util.formatCurrency(user.getMoney(), ess)));
			}
			sender.sendMessage(user.isAfk()
					? Util.i18n("whoisStatusAway")
					: Util.i18n("whoisStatusAvailable"));
			sender.sendMessage(Util.format("whoisIPAddress", user.getAddress().getAddress().toString()));
			final String location = user.getGeoLocation();
			if (location != null
				&& (sender instanceof Player ? ess.getUser(sender).isAuthorized("essentials.geoip.show") : true))
			{
				sender.sendMessage(Util.format("whoisGeoLocation", location));
			}
		}
	}
}
