package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandext extends EssentialsCommand
{
	public Commandext()
	{
		super("ext");
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		extinguishPlayers(server, sender, args[0]);
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.setFireTicks(0);
			user.sendMessage(_("extinguish"));
			return;
		}

		if (args[0].trim().length() < 2)
		{
			throw new PlayerNotFoundException();
		}

		extinguishPlayers(server, user.getBase(), args[0]);
	}

	private void extinguishPlayers(final Server server, final CommandSender sender, final String name) throws Exception
	{
		boolean skipHidden = sender instanceof Player && !ess.getUser(sender).isAuthorized("essentials.vanish.interact");
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
			matchPlayer.setFireTicks(0);
			sender.sendMessage(_("extinguishOthers", matchPlayer.getDisplayName()));
		}
		if (!foundUser)
		{
			throw new PlayerNotFoundException();
		}
	}
}
