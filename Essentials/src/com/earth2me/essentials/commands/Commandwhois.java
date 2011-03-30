package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
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
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage("§cUsage: /whois [nickname]");
			return;
		}
		String whois = args[0].toLowerCase();
		User.charge(sender, this);
		int prefixLength = ChatColor.stripColor(Essentials.getSettings().getNicknamePrefix()).length();
		for (Player p : server.getOnlinePlayers())
		{
			User u = User.get(p);
			String dn = ChatColor.stripColor(u.getNick());
			if (!whois.equalsIgnoreCase(dn) && !whois.equalsIgnoreCase(dn.substring(prefixLength)) && !whois.equalsIgnoreCase(u.getName())) continue;
			sender.sendMessage("");
			sender.sendMessage(u.getDisplayName() + " is " + u.getName());
			sender.sendMessage(ChatColor.BLUE + " - Health: " + u.getHealth() + "/20");
			sender.sendMessage(ChatColor.BLUE + " - Location: (" + u.getLocation().getWorld().getName() + ", " + u.getLocation().getBlockX() + ", " + u.getLocation().getBlockY() + ", " + u.getLocation().getBlockZ() + ")");
			if (!parent.getConfiguration().getBoolean("disable-eco", false)) sender.sendMessage(ChatColor.BLUE + " - Money: $" + u.getMoney());
			sender.sendMessage(ChatColor.BLUE + " - Status: " + (parent.away.contains(u) ? "§cAway§f" : "Available"));
			sender.sendMessage(ChatColor.BLUE + " - IP Address: " + u.getAddress().getAddress().toString());

		}
	}
}
