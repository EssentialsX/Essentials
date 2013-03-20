package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;


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
		healPlayer(user);
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

	private void healOtherPlayers(final Server server, final CommandSender sender, final String name) throws Exception
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
			healPlayer(matchPlayer);
			sender.sendMessage(_("healOther", matchPlayer.getDisplayName()));
		}
		if (!foundUser)
		{
			throw new NotEnoughArgumentsException(_("playerNotFound"));
		}
	}

	private void healPlayer(final Player player) throws Exception
	{
		if (player.getHealth() == 0)
		{
			throw new Exception(_("healDead"));
		}
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.sendMessage(_("heal"));
		for (PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
	}
}
