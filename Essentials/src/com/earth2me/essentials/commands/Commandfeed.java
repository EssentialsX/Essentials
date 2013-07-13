package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;


public class Commandfeed extends EssentialsCommand
{
	public Commandfeed()
	{
		super("feed");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0 && user.isAuthorized("essentials.feed.others"))
		{
			if (args[0].trim().length() < 2)
			{
				throw new PlayerNotFoundException();
			}
			if (!user.isAuthorized("essentials.heal.cooldown.bypass"))
			{
				user.healCooldown();
			}
			feedOtherPlayers(server, user.getBase(), args[0]);
			return;
		}

		if (!user.isAuthorized("essentials.heal.cooldown.bypass"))
		{
			user.healCooldown();
		}
		try
		{
			feedPlayer(user.getBase(), user.getBase());
		}
		catch (QuietAbortException e)
		{
			//User does not need feeding.
		}

		user.sendMessage(_("feed"));
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		feedOtherPlayers(server, sender, args[0]);
	}

	private void feedOtherPlayers(final Server server, final CommandSender sender, final String name) throws PlayerNotFoundException
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
			try
			{
				feedPlayer(sender, matchPlayer);
			}
			catch (QuietAbortException e)
			{
				//User does not need feeding.
			}
		}
		if (!foundUser)
		{
			throw new PlayerNotFoundException();
		}
	}

	private void feedPlayer(CommandSender sender, Player player) throws QuietAbortException
	{
		final int amount = 30;

		final FoodLevelChangeEvent flce = new FoodLevelChangeEvent(player, amount);
		ess.getServer().getPluginManager().callEvent(flce);
		if (flce.isCancelled())
		{
			throw new QuietAbortException();
		}

		player.setFoodLevel(flce.getFoodLevel() > 20 ? 20 : flce.getFoodLevel());
		player.setSaturation(10);

		if (!sender.equals(player))
		{
			sender.sendMessage(_("feedOther", player.getDisplayName()));
		}
	}
}
