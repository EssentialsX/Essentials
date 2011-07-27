package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import com.earth2me.essentials.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.entity.Player;


public class Commandtempban extends EssentialsCommand
{
	public Commandtempban()
	{
		super("tempban");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		final User player = getPlayer(server, args, 0, true);
		if (player.getBase() instanceof OfflinePlayer)
		{
			if (sender instanceof Player
				&& !ess.getUser(sender).isAuthorized("essentials.tempban.offline"))
			{
				sender.sendMessage(Util.i18n("tempbanExempt"));
				return;
			}
		}
		else
		{
			if (player.isAuthorized("essentials.tempban.exempt"))
			{
				sender.sendMessage(Util.i18n("tempbanExempt"));
				return;
			}
		}
		final String time = getFinalArg(args, 1);
		final long banTimestamp = Util.parseDateDiff(time, true);

		final String banReason = Util.format("tempBanned", Util.formatDateDiff(banTimestamp));
		player.setBanReason(banReason);
		player.setBanTimeout(banTimestamp);
		player.kickPlayer(banReason);
		ess.getBans().banByName(player.getName());
		String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;
		
		for(Player p : server.getOnlinePlayers())
		{
			User u = ess.getUser(p);
			if(u.isAuthorized("essentials.ban.notify"))
			{
			p.sendMessage(Util.format("playerBanned", senderName, player.getName(), banReason));
			}
		}
	}
}
