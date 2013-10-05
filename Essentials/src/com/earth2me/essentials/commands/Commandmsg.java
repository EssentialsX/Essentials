package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IReplyTo;
import com.earth2me.essentials.User;
import static com.earth2me.essentials.commands.EssentialsCommand.getFinalArg;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandmsg extends EssentialsLoopCommand
{
	final String translatedMe = _("me");

	public Commandmsg()
	{
		super("msg");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2 || args[0].trim().length() < 2 || args[1].trim().isEmpty())
		{
			throw new NotEnoughArgumentsException();
		}

		String message = getFinalArg(args, 1);
		if (sender instanceof Player)
		{
			User user = ess.getUser(sender);
			if (user.isMuted())
			{
				throw new Exception(_("voiceSilenced"));
			}
			message = FormatUtil.formatMessage(user, "essentials.msg", message);
		}
		else
		{
			message = FormatUtil.replaceFormat(message);
		}

		if (args[0].equalsIgnoreCase(Console.NAME))
		{
			final IReplyTo replyTo = sender instanceof Player ? ess.getUser(sender) : Console.getConsoleReplyTo();
			final String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;
			
			sender.sendMessage(_("msgFormat", translatedMe, Console.NAME, message));
			CommandSender cs = Console.getCommandSender(server);
			cs.sendMessage(_("msgFormat", senderName, translatedMe, message));
			replyTo.setReplyTo(cs);
			Console.getConsoleReplyTo().setReplyTo(sender);
			return;
		}

		loopOnlinePlayers(server, sender, false, args[0], new String[]{message});
	}

	@Override
	protected void updatePlayer(final Server server, final CommandSender sender, final User matchedUser, final String[] args)
	{		
		final IReplyTo replyTo = sender instanceof Player ? ess.getUser(sender) : Console.getConsoleReplyTo();
		final String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;

		if (matchedUser.isAfk())
		{
			sender.sendMessage(_("userAFK", matchedUser.getDisplayName()));
		}

		sender.sendMessage(_("msgFormat", translatedMe, matchedUser.getDisplayName(), args[0]));
		if (sender instanceof Player && matchedUser.isIgnoredPlayer(ess.getUser(sender)))
		{
			return;
		}

		matchedUser.sendMessage(_("msgFormat", senderName, translatedMe, args[0]));
		replyTo.setReplyTo(matchedUser.getBase());
		matchedUser.setReplyTo(sender);
	}
}
