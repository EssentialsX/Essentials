package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.List;
import java.util.Locale;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;


public class Commandgamemode extends EssentialsCommand
{
	public Commandgamemode()
	{
		super("gamemode");
	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		GameMode gameMode;
		if (args.length == 0)
		{
			throw new NotEnoughArgumentsException();
		}
		else if (args.length == 1)
		{
			gameMode = matchGameMode(commandLabel);
			gamemodeOtherPlayers(server, sender, gameMode, args[0]);
		}
		else if (args.length == 2)
		{
			gameMode = matchGameMode(args[0].toLowerCase(Locale.ENGLISH));
			gamemodeOtherPlayers(server, sender, gameMode, args[1]);
		}

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
			gamemodeOtherPlayers(server, user.getSource(), gameMode, args[1]);
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
					gamemodeOtherPlayers(server, user.getSource(), gameMode, args[0]);
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

	private void gamemodeOtherPlayers(final Server server, final CommandSource sender, final GameMode gameMode, final String name) throws NotEnoughArgumentsException, PlayerNotFoundException
	{
		if (name.trim().length() < 2 || gameMode == null)
		{
			throw new NotEnoughArgumentsException(_("gameModeInvalid"));
		}

		boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.vanish.interact");
		boolean foundUser = false;
		final List<Player> matchedPlayers = server.matchPlayer(name);
		for (Player matchPlayer : matchedPlayers)
		{
			final User player = ess.getUser(matchPlayer);
			if (skipHidden && player.isHidden())
			{
				continue;
			}
			foundUser = true;
			player.setGameMode(gameMode);
			sender.sendMessage(_("gameMode", _(player.getGameMode().toString().toLowerCase(Locale.ENGLISH)), player.getDisplayName()));
		}
		if (!foundUser)
		{
			throw new PlayerNotFoundException();
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