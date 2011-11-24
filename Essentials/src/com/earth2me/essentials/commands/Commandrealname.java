package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandrealname extends EssentialsCommand
{
	public Commandrealname()
	{
		super("realname");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final String whois = args[0].toLowerCase(Locale.ENGLISH);
		for (Player p : server.getOnlinePlayers())
		{
			final User u = ess.getUser(p);
			if (u.isHidden())
			{
				continue;
			}
			final String displayName = ChatColor.stripColor(u.getDisplayName()).toLowerCase(Locale.ENGLISH);
			if (!whois.equals(displayName)
				&& !displayName.equals(ChatColor.stripColor(ess.getSettings().getNicknamePrefix()) + whois)
				&& !whois.equalsIgnoreCase(u.getName()))
			{
				continue;
			}
			sender.sendMessage(u.getDisplayName() + " " + _("is") + " " + u.getName());
		}
	}
}
