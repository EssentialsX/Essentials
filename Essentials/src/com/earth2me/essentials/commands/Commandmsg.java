package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IReplyTo;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
import java.util.List;
import lombok.Cleanup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandmsg extends EssentialsCommand
{
	@Override
	public void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2 || args[0].trim().isEmpty() || args[1].trim().isEmpty())
		{
			throw new NotEnoughArgumentsException();
		}

		String message = getFinalArg(args, 1);
		if (sender instanceof Player)
		{
			@Cleanup
			IUser user = ess.getUser((Player)sender);
			user.acquireReadLock();
			if (user.getData().isMuted())
			{
				throw new Exception(_("voiceSilenced"));
			}
			if (Permissions.MSG_COLOR.isAuthorized(user))
			{
				message = Util.replaceColor(message);
			}
			else
			{
				message = Util.stripColor(message);
			}
		}
		else
		{
			message = Util.replaceColor(message);
		}

		final String translatedMe = _("me");

		final IReplyTo replyTo = sender instanceof Player ? ess.getUser((Player)sender) : Console.getConsoleReplyTo();
		final String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;

		if (args[0].equalsIgnoreCase(Console.NAME))
		{
			sender.sendMessage(_("msgFormat", translatedMe, Console.NAME, message));
			CommandSender cs = server.getConsoleSender();
			cs.sendMessage(_("msgFormat", senderName, translatedMe, message));
			replyTo.setReplyTo(cs);
			Console.getConsoleReplyTo().setReplyTo(sender);
			return;
		}

		final List<Player> matchedPlayers = server.matchPlayer(args[0]);

		if (matchedPlayers.isEmpty())
		{
			throw new Exception(_("playerNotFound"));
		}

		int i = 0;
		for (Player matchedPlayer : matchedPlayers)
		{
			final IUser u = ess.getUser(matchedPlayer);
			if (u.isHidden())
			{
				i++;
			}
		}
		if (i == matchedPlayers.size())
		{
			throw new Exception(_("playerNotFound"));
		}

		for (Player matchedPlayer : matchedPlayers)
		{
			sender.sendMessage(_("msgFormat", translatedMe, matchedPlayer.getDisplayName(), message));
			final IUser matchedUser = ess.getUser(matchedPlayer);
			if (sender instanceof Player && (matchedUser.isIgnoringPlayer(((Player)sender).getName()) || matchedUser.isHidden()))
			{
				continue;
			}
			matchedPlayer.sendMessage(_("msgFormat", senderName, translatedMe, message));
			replyTo.setReplyTo(ess.getUser(matchedPlayer));
			ess.getUser(matchedPlayer).setReplyTo(sender);
		}
	}
}
