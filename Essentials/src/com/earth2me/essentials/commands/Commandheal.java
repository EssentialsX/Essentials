package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
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
		user.sendMessage(_("heal"));
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
			sender.sendMessage(_("playerNotFound"));
			return;
		}
		for (Player p : players)
		{
			if (ess.getUser(p).isHidden())
			{
				continue;
			}
			p.setHealth(20);
			p.setFoodLevel(20);
			p.sendMessage(_("heal"));
			sender.sendMessage(_("healOther", p.getDisplayName()));
		}
	}
}
