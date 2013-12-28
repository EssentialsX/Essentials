package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IReplyTo;
import com.earth2me.essentials.User;
import static com.earth2me.essentials.commands.EssentialsCommand.getFinalArg;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandmsg extends EssentialsLoopCommand
{
	final String translatedMe = _("me");

	public Commandmsg()
	{
		super("msg");
	}

	@Override
	public void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2 || args[0].trim().length() < 2 || args[1].trim().isEmpty())
		{
			throw new NotEnoughArgumentsException();
		}

		String message = getFinalArg(args, 1);
		boolean canWildcard;
		if (sender.isPlayer())
		{
			User user = ess.getUser(sender.getPlayer());
			if (user.isMuted())
			{
				throw new Exception(_("voiceSilenced"));
			}
			message = FormatUtil.formatMessage(user, "essentials.msg", message);
			canWildcard = user.isAuthorized("essentials.msg.multiple");
		}
		else
		{
			message = FormatUtil.replaceFormat(message);
			canWildcard = true;
		}

		if (args[0].equalsIgnoreCase(Console.NAME))
		{
			final IReplyTo replyTo = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : Console.getConsoleReplyTo();
			final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
			
			sender.sendMessage(_("msgFormat", translatedMe, Console.NAME, message));
			CommandSender cs = Console.getCommandSender(server);
			cs.sendMessage(_("msgFormat", senderName, translatedMe, message));
			replyTo.setReplyTo(new CommandSource(cs));
			Console.getConsoleReplyTo().setReplyTo(sender);
			return;
		}

		loopOnlinePlayers(server, sender, canWildcard, canWildcard, args[0], new String[]{message});
	}

	@Override
	protected void updatePlayer(final Server server, final CommandSource sender, final User matchedUser, final String[] args)
	{		
		final IReplyTo replyTo = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : Console.getConsoleReplyTo();
		final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;

		if (matchedUser.isAfk())
		{
			sender.sendMessage(_("userAFK", matchedUser.getDisplayName()));
		}

		sender.sendMessage(_("msgFormat", translatedMe, matchedUser.getDisplayName(), args[0]));
		if (sender.isPlayer() && matchedUser.isIgnoredPlayer(ess.getUser(sender.getPlayer())))
		{
			return;
		}

		matchedUser.sendMessage(_("msgFormat", senderName, translatedMe, args[0]));
		replyTo.setReplyTo(matchedUser.getSource());
		matchedUser.setReplyTo(sender);
	}
}
