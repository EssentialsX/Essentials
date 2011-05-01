package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
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
			sender.sendMessage("§cUsage: /whois [nickname]");
			return;
		}
		String whois = args[0].toLowerCase();
		charge(sender);
		int prefixLength = ChatColor.stripColor(ess.getSettings().getNicknamePrefix()).length();
		for (Player p : server.getOnlinePlayers())
		{
			User u = ess.getUser(p);
			String dn = ChatColor.stripColor(u.getNick());
			if (!whois.equalsIgnoreCase(dn)
				&& !whois.equalsIgnoreCase(dn.substring(prefixLength))
				&& !whois.equalsIgnoreCase(u.getName()))
			{
				continue;
			}
			sender.sendMessage("");
			sender.sendMessage(u.getDisplayName() + " is " + u.getName());
			sender.sendMessage(ChatColor.BLUE + " - Health: " + u.getHealth() + "/20");
			sender.sendMessage(ChatColor.BLUE + " - Location: (" + u.getLocation().getWorld().getName() + ", " + u.getLocation().getBlockX() + ", " + u.getLocation().getBlockY() + ", " + u.getLocation().getBlockZ() + ")");
			if (!ess.getConfiguration().getBoolean("disable-eco", false))
			{
				sender.sendMessage(ChatColor.BLUE + " - Money: $" + u.getMoney());
			}
			sender.sendMessage(ChatColor.BLUE + " - Status: " + (u.isAfk() ? "§cAway§f" : "Available"));
			sender.sendMessage(ChatColor.BLUE + " - IP Address: " + u.getAddress().getAddress().toString());
			String location = u.getGeoLocation();
			if (location != null 
				&& (sender instanceof Player ? ess.getUser(sender).isAuthorized("essentials.geoip.show") : true))
			{
				sender.sendMessage(ChatColor.BLUE + " - Location: " + location.toString());
			}
		}
	}
}
