package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import java.util.logging.Level;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandbanip extends EssentialsCommand
{
	public Commandbanip()
	{
		super("banip");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final User player = ess.getUser(args[0]);

		if (player == null)
		{
			ess.getServer().banIP(args[0]);
			sender.sendMessage(_("banIpAddress"), senderName, args[0]);
		}
		else
		{
			final String ipAddress = player.getLastLoginAddress();
			if (ipAddress.length() == 0)
			{
				throw new Exception(_("playerNotFound"));
			}
			ess.getServer().banIP(ipAddress);
			final String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;
			server.getLogger().log(Level.INFO, _("banIpAddress", senderName, ipAddress));
			for (Player onlinePlayer : server.getOnlinePlayers())
			{
				final User player = ess.getUser(onlinePlayer);
				if (onlinePlayer == sender || player.isAuthorized("essentials.ban.notify"))
				{
					sender.sendMessage(_("banIpAddress", senderName, ipAddress));
				}
			}
		}
	}
}
