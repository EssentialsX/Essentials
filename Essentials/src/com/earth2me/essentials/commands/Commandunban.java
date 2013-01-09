package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandunban extends EssentialsCommand
{
	public Commandunban()
	{
		super("unban");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		try
		{
			final User user = getPlayer(server, args, 0, true);
			user.setBanned(false);
			user.setBanTimeout(0);
			sender.sendMessage(_("unbannedPlayer"));
		}
		catch (NoSuchFieldException e)
		{
			final OfflinePlayer player = server.getOfflinePlayer(args[0]);
			if (player.isBanned()) {
				player.setBanned(false);
				sender.sendMessage(_("unbannedPlayer"));
				return;
			}			
			
			throw new Exception(_("playerNotFound"), e);
		}
	}
}
