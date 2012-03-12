package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandban extends EssentialsCommand
{
	public Commandban()
	{
		super("ban");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final User user = getPlayer(server, args, 0, true);
		if (!user.isOnline())
		{
			if (sender instanceof Player
				&& !ess.getUser(sender).isAuthorized("essentials.ban.offline"))
			{
				sender.sendMessage(_("banExempt"));
				return;
			}
		}
		else
		{
			if (user.isAuthorized("essentials.ban.exempt"))
			{
				sender.sendMessage(_("banExempt"));
				return;
			}
		}

		String banReason;
		if (args.length > 1)
		{
			banReason = getFinalArg(args, 1);
			user.setBanReason(banReason);
		}
		else
		{
			banReason = _("defaultBanReason");
		}
		user.setBanned(true);
		user.kickPlayer(banReason);
		final String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;

		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final User player = ess.getUser(onlinePlayer);
			if (player.isAuthorized("essentials.ban.notify"))
			{
				onlinePlayer.sendMessage(_("playerBanned", senderName, user.getName(), banReason));
			}
		}
	}
}
