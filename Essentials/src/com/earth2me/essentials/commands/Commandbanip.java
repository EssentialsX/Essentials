package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
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

		final String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;
		final String ipAddress;

		final User player = ess.getUser(args[0]);
		if (player == null)
		{
			ipAddress = args[0];
		}
		else
		{
			ipAddress = player.getLastLoginAddress();
			if (ipAddress.length() == 0)
			{
				throw new Exception(_("playerNotFound"));
			}
		}

		ess.getServer().banIP(ipAddress);
		server.getLogger().log(Level.INFO, _("playerBanIpAddress", senderName, ipAddress));
		
		ess.broadcastMessage(sender, "essentials.ban.notify", _("playerBanIpAddress", senderName, ipAddress));		
	}
}
