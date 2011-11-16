package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandgamemode extends EssentialsCommand
{
	public Commandgamemode()
	{
		super("gamemode");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		Player player;
		if (args.length == 0)
		{
			if (sender instanceof Player)
			{
				player = ess.getUser(sender);			}
			else
			{
				throw new NotEnoughArgumentsException();
			}
		}
		else
		{
			player = server.getPlayer(args[0]);
			if (player == null)
			{
				throw new Exception(Util.i18n("playerNotFound"));
			}
		}
		player.setGameMode(player.getGameMode() == GameMode.SURVIVAL ? GameMode.CREATIVE : GameMode.SURVIVAL);
		sender.sendMessage(Util.format("gameMode", Util.i18n(player.getGameMode().toString().toLowerCase()), player.getDisplayName()));
	}
}
