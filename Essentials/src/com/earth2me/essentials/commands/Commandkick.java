package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandkick extends EssentialsCommand
{
	public Commandkick()
	{
		super("kick");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final User user = getPlayer(server, args, 0);
		if (user.isAuthorized("essentials.kick.exempt"))
		{
			throw new Exception(Util.i18n("kickExempt"));
		}
		final String kickReason = args.length > 1 ? getFinalArg(args, 1) : Util.i18n("kickDefault");
		user.kickPlayer(kickReason);
		final String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;

		for(Player onlinePlayer : server.getOnlinePlayers())
		{
			User player = ess.getUser(onlinePlayer);
			if(player.isAuthorized("essentials.kick.notify"))
			{
			onlinePlayer.sendMessage(Util.format("playerKicked", senderName, user.getName(), kickReason));
			}
		}
	}
}
