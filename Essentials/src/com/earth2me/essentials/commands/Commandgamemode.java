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
		GameMode gameMode = matchGameMode(args[0].toLowerCase(Locale.ENGLISH));
		gamemodeOtherPlayers(server, sender, gameMode, args[1]);
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		GameMode gameMode;
		if (args.length == 0)
		{
			gameMode = matchGameMode(commandLabel);
		}
		else if (args.length > 1 && args[1].trim().length() > 2 && user.isAuthorized("essentials.gamemode.others"))
		{
			gameMode = matchGameMode(args[0].toLowerCase(Locale.ENGLISH));
			gamemodeOtherPlayers(server, user, gameMode, args[1]);
			return;
		}
		else 
		{
			try
			{
				gameMode = matchGameMode(args[0].toLowerCase(Locale.ENGLISH));
			}
			catch (NotEnoughArgumentsException e)
			{
				if (user.isAuthorized("essentials.gamemode.others"))
				{
					gameMode = matchGameMode(commandLabel);
					gamemodeOtherPlayers(server, user, gameMode, args[0]);
					return;
				}
				throw new NotEnoughArgumentsException();
			}
		}
		if (gameMode == null)
		{
			gameMode = user.getGameMode() == GameMode.SURVIVAL ? GameMode.CREATIVE : user.getGameMode() == GameMode.CREATIVE ? GameMode.ADVENTURE : GameMode.SURVIVAL;
		}
		user.setGameMode(gameMode);
		user.sendMessage(_("gameMode", _(user.getGameMode().toString().toLowerCase(Locale.ENGLISH)), user.getDisplayName()));
	}

	private void gamemodeOtherPlayers(final Server server, final CommandSender sender, final GameMode gameMode, final String player) throws NotEnoughArgumentsException
	{
		//TODO: TL this
		if (player.trim().length() < 2 || gameMode == null)
		{
			throw new NotEnoughArgumentsException("You need to specify a valid player/mode.");
		}

		boolean foundUser = false;
		for (Player matchPlayer : server.matchPlayer(player))
		{
			final User user = ess.getUser(matchPlayer);
			if (user.isHidden())
			{
				continue;
			}
			user.setGameMode(gameMode);
			sender.sendMessage(_("gameMode", _(user.getGameMode().toString().toLowerCase(Locale.ENGLISH)), user.getDisplayName()));
			foundUser = true;
		}
		if (!foundUser)
		{
			throw new NotEnoughArgumentsException(_("playerNotFound"));
		}
	}

	private GameMode matchGameMode(String modeString) throws NotEnoughArgumentsException
	{
		GameMode mode = null;
		if (modeString.equalsIgnoreCase("gmc") || modeString.equalsIgnoreCase("egmc")
			|| modeString.contains("creat") || modeString.equalsIgnoreCase("1") || modeString.equalsIgnoreCase("c"))
		{
			mode = GameMode.CREATIVE;
		}
		else if (modeString.equalsIgnoreCase("gms") || modeString.equalsIgnoreCase("egms")
				 || modeString.contains("survi") || modeString.equalsIgnoreCase("0") || modeString.equalsIgnoreCase("s"))
		{
			mode = GameMode.SURVIVAL;
		}
		else if (modeString.equalsIgnoreCase("gma") || modeString.equalsIgnoreCase("egma")
				 || modeString.contains("advent") || modeString.equalsIgnoreCase("2") || modeString.equalsIgnoreCase("a"))
		{
			mode = GameMode.ADVENTURE;
		}
		else if (modeString.equalsIgnoreCase("gmt") || modeString.equalsIgnoreCase("egmt")
				 || modeString.contains("toggle") || modeString.contains("cycle") || modeString.equalsIgnoreCase("t"))
		{
			mode = null;
		}
		else
		{
			throw new NotEnoughArgumentsException();
		}
		return mode;
	}
}