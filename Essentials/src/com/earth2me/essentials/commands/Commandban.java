package com.earth2me.essentials.commands;

import com.earth2me.essentials.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
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
		final User player = getPlayer(server, args, 0, true);
		if (player.getBase() instanceof OfflinePlayer)
		{
			if (sender instanceof Player
				&& !ess.getUser(sender).isAuthorized("essentials.ban.offline"))
			{
				sender.sendMessage(Util.i18n("banExempt"));
				return;
			}
		}
		else
		{
			if (player.isAuthorized("essentials.ban.exempt"))
			{
				sender.sendMessage(Util.i18n("banExempt"));
				return;
			}
		}

		String banReason;
		if (args.length > 1)
		{
			banReason = getFinalArg(args, 1);
			player.setBanReason(commandLabel);
		}
		else
		{
			banReason = Util.i18n("defaultBanReason");
		}
		player.kickPlayer(banReason);
		ess.getBans().banByName(player.getName());
		server.broadcastMessage(Util.format("playerBanned", player.getName(), banReason));
	}
}
