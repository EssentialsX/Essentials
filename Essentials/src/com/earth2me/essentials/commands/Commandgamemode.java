package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.Locale;
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
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		gamemodeOtherPlayers(server, sender, args);
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0 && args[0].trim().length() > 2 && user.isAuthorized("essentials.gamemode.others"))
		{
			gamemodeOtherPlayers(server, user, args);
			return;
		}

		user.setGameMode(user.getGameMode() == GameMode.SURVIVAL ? GameMode.CREATIVE : GameMode.SURVIVAL);
		user.sendMessage(_("gameMode", _(user.getGameMode().toString().toLowerCase(Locale.ENGLISH)), user.getDisplayName()));
	}

	private void gamemodeOtherPlayers(final Server server, final CommandSender sender, final String[] args)
	{
		for (Player matchPlayer : server.matchPlayer(args[0]))
		{
			final User player = ess.getUser(matchPlayer);
			if (player.isHidden())
			{
				continue;
			}

			if (args.length > 1)
			{
				if (args[1].contains("creat") || args[1].equalsIgnoreCase("1"))
				{
					player.setGameMode(GameMode.CREATIVE);
				}
				else
				{
					player.setGameMode(GameMode.SURVIVAL);
				}
			}
			else
			{
				player.setGameMode(player.getGameMode() == GameMode.SURVIVAL ? GameMode.CREATIVE : GameMode.SURVIVAL);
			}
			sender.sendMessage(_("gameMode", _(player.getGameMode().toString().toLowerCase(Locale.ENGLISH)), player.getDisplayName()));
		}
	}
}
