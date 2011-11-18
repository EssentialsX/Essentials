package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandheal extends EssentialsCommand
{
	public Commandheal()
	{
		super("heal");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{

		if (args.length > 0 && user.isAuthorized("essentials.heal.others"))
		{
			if (!user.isAuthorized("essentials.heal.cooldown.bypass"))
			{
				user.healCooldown();
			}
			healOtherPlayers(server, user, args[0]);
			return;
		}

		if (!user.isAuthorized("essentials.heal.cooldown.bypass"))
		{
			user.healCooldown();
		}
		user.setHealth(20);
		user.setFoodLevel(20);
		user.sendMessage(Util.i18n("heal"));
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		healOtherPlayers(server, sender, args[0]);
	}

	private void healOtherPlayers(final Server server, final CommandSender sender, final String name)
	{
		final List<Player> players = server.matchPlayer(name);
		if (players.isEmpty())
		{
			sender.sendMessage(Util.i18n("playerNotFound"));
			return;
		}
		for (Player p : players)
		{
			if (ess.getUser(p).isHidden())
			{
				continue;
			}
			p.setHealth(20);
			sender.sendMessage(Util.format("healOther", p.getDisplayName()));
		}
	}
}
