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
			feedOtherPlayers(server, user, args[0]);
		}
		else
		{
			feedPlayer(user, user);
		}
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

	private void feedOtherPlayers(final Server server, final CommandSender sender, final String name) throws NotEnoughArgumentsException, QuietAbortException
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
			feedPlayer(sender, matchPlayer);

		}
		if (!foundUser)
		{
			throw new NotEnoughArgumentsException(_("playerNotFound"));
		}
	}

	private void feedPlayer(CommandSender sender, Player player) throws QuietAbortException
	{
		final int amount = 100;

		final FoodLevelChangeEvent flce = new FoodLevelChangeEvent(player, amount);
		ess.getServer().getPluginManager().callEvent(flce);
		if (flce.isCancelled())
		{
			throw new QuietAbortException();
		}

		player.setFoodLevel(flce.getFoodLevel());
		player.setSaturation(10);
		sender.sendMessage(sender.equals(player) ? _("feed") : _("feedOther", player.getDisplayName()));
	}
}
