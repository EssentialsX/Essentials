package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.ChatColor;


public class Commandwhois extends EssentialsCommand
{
	public Commandwhois()
	{
		super("whois");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
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
		String whois = args[0].toLowerCase();
		int prefixLength = ChatColor.stripColor(ess.getSettings().getNicknamePrefix()).length();
		for (Player p : server.getOnlinePlayers())
		{
			User u = ess.getUser(p);
			if (u.isHidden() && !showhidden)
			{
				continue;
			}
			String dn = ChatColor.stripColor(u.getNick());
			if (!whois.equalsIgnoreCase(dn)
				&& !whois.equalsIgnoreCase(dn.substring(prefixLength))
				&& !whois.equalsIgnoreCase(u.getName()))
			{
				continue;
			}
			sender.sendMessage("");
			sender.sendMessage(Util.format("whoisIs", u.getDisplayName(), u.getName()));
			sender.sendMessage(Util.format("whoisHealth", u.getHealth()));
			sender.sendMessage(Util.format("whoisLocation", u.getLocation().getWorld().getName(), u.getLocation().getBlockX(), u.getLocation().getBlockY(), u.getLocation().getBlockZ()));
			if (!ess.getSettings().isEcoDisabled())
			{
				sender.sendMessage(Util.format("whoisMoney", Util.formatCurrency(u.getMoney(), ess)));
			}
			sender.sendMessage(u.isAfk() 
					? Util.i18n("whoisStatusAway") 
					: Util.i18n("whoisStatusAvailable"));
			sender.sendMessage(Util.format("whoisIPAddress", u.getAddress().getAddress().toString()));
			final String location = u.getGeoLocation();
			if (location != null 
				&& (sender instanceof Player ? ess.getUser(sender).isAuthorized("essentials.geoip.show") : true))
			{
				sender.sendMessage(Util.format("whoisGeoLocation", location));
			}
		}
	}
}
