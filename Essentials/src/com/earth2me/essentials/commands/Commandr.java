package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IReplyTo;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandr extends EssentialsCommand
{
	@Override
	public void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		String message = getFinalArg(args, 0);
		IReplyTo replyTo;
		String senderName;

		if (sender instanceof Player)
		{
			IUser user = ess.getUser((Player)sender);
			if (Permissions.MSG_COLOR.isAuthorized(user))
			{
				message = Util.replaceColor(message);
			}
			else
			{
				message = Util.stripColor(message);
			}
			replyTo = user;
			senderName = user.getDisplayName();
		}
		else
		{
			message = Util.replaceColor(message);
			replyTo = Console.getConsoleReplyTo();
			senderName = Console.NAME;
		}

		final CommandSender target = replyTo.getReplyTo();
		final String targetName = target instanceof Player ? ((Player)target).getDisplayName() : Console.NAME;

		if (target == null || ((target instanceof Player) && !((Player)target).isOnline()))
		{
			throw new Exception(_("foreverAlone"));
		}

		sender.sendMessage(_("msgFormat", _("me"), targetName, message));
		if (target instanceof Player)
		{
			IUser player = ess.getUser((Player)target);
			if (player.isIgnoringPlayer(sender instanceof Player ? ((Player)sender).getName() : Console.NAME))
			{
				return;
			}
		}
		target.sendMessage(_("msgFormat", senderName, _("me"), message));
		replyTo.setReplyTo(target);
		if (target != sender)
		{
			if (target instanceof Player)
			{
				ess.getUser((Player)target).setReplyTo(sender);
			}
			else
			{
				Console.getConsoleReplyTo().setReplyTo(sender);
			}
		}
	}

	@Override
	public String getPermission()
	{
		return "essentials.msg";
	}
}
