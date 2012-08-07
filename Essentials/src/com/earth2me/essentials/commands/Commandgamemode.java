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
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		gamemodeOtherPlayers(server, sender, args);
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		if (args.length > 1 && args[1].trim().length() > 2 && user.isAuthorized("essentials.gamemode.others"))
		{
			gamemodeOtherPlayers(server, user, args);
			return;
		}
		performSetMode(args[0].toLowerCase(Locale.ENGLISH), user);
		user.sendMessage(_("gameMode", _(user.getGameMode().toString().toLowerCase(Locale.ENGLISH)), user.getDisplayName()));
	}

	private void gamemodeOtherPlayers(final Server server, final CommandSender sender, final String[] args)
	{
		for (Player matchPlayer : server.matchPlayer(args[1]))
		{
			final User player = ess.getUser(matchPlayer);
			if (player.isHidden())
			{
				continue;
			}
			performSetMode(args[0].toLowerCase(Locale.ENGLISH), player);
			sender.sendMessage(_("gameMode", _(player.getGameMode().toString().toLowerCase(Locale.ENGLISH)), player.getDisplayName()));
		}
	}

	private void performSetMode(String mode, Player player)
	{
		if (mode.contains("survi") || mode.equalsIgnoreCase("0") || mode.equalsIgnoreCase("s"))
		{
			player.setGameMode(GameMode.SURVIVAL);
		}
		else if (mode.contains("creat") || mode.equalsIgnoreCase("1") || mode.equalsIgnoreCase("c"))
		{
			player.setGameMode(GameMode.CREATIVE);
		}
		else if (mode.contains("advent") || mode.equalsIgnoreCase("2") || mode.equalsIgnoreCase("a"))
		{
			player.setGameMode(GameMode.ADVENTURE);
		}
	}
}