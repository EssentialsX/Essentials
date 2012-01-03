package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.user.UserData.TimestampType;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandheal extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String[] args) throws Exception
	{

		if (args.length > 0 && user.isAuthorized("essentials.heal.others"))
		{
			user.checkCooldown(TimestampType.LASTHEAL, ess.getGroups().getHealCooldown(user), true, "essentials.heal.cooldown.bypass");

			healOtherPlayers(user, args[0]);
			return;
		}

		user.checkCooldown(TimestampType.LASTHEAL, ess.getGroups().getHealCooldown(user), true, "essentials.heal.cooldown.bypass");

		user.setHealth(20);
		user.setFoodLevel(20);
		user.sendMessage(_("heal"));
	}

	@Override
	public void run(final CommandSender sender, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		healOtherPlayers(sender, args[0]);
	}

	private void healOtherPlayers(final CommandSender sender, final String name)
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
