package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import java.util.logging.Level;
import org.bukkit.Server;


public class Commandunbanip extends EssentialsCommand
{
	public Commandunbanip()
	{
		super("unbanip");
	}

	@Override
	public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		
		String ipAddress;
		if (FormatUtil.validIP(args[0]))
		{
			ipAddress = args[0];
		}
		else
		{
			try
			{
				User player = getPlayer(server, args, 0, true, true);
				ipAddress = player.getLastLoginAddress();
			}
			catch (PlayerNotFoundException ex)
			{
				ipAddress = args[0];
			}
		}

		if (ipAddress.isEmpty())
		{
			throw new PlayerNotFoundException();
		}

		ess.getServer().unbanIP(ipAddress);
		final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
		server.getLogger().log(Level.INFO, _("playerUnbanIpAddress", senderName, ipAddress));

		ess.broadcastMessage("essentials.ban.notify", _("playerUnbanIpAddress", senderName, ipAddress));
	}
}
