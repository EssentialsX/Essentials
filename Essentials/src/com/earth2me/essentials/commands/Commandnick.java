package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import java.util.Locale;
import net.ess3.api.events.LocalChatSpyEvent;
import net.ess3.api.events.NickChangeEvent;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandnick extends EssentialsLoopCommand
{
	public Commandnick()
	{
		super("nick");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		if (!ess.getSettings().changeDisplayName())
		{
			throw new Exception(_("nickDisplayName"));
		}

		final String[] nickname = formatNickname(user, args[1]).split(" ");
		if (args.length > 1 && user.isAuthorized("essentials.nick.others"))
		{
			loopOfflinePlayers(server, user.getBase(), false, args[0], nickname);
			user.sendMessage(_("nickChanged"));
		}
		else
		{
			updatePlayer(server, user.getBase(), user, nickname);
		}
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		if (!ess.getSettings().changeDisplayName())
		{
			throw new Exception(_("nickDisplayName"));
		}
		final String[] nickname = formatNickname(null, args[1]).split(" ");
		loopOfflinePlayers(server, sender, false, args[0], nickname);
		sender.sendMessage(_("nickChanged"));
	}

	@Override
	protected void updatePlayer(final Server server, final CommandSender sender, final User target, final String[] args) throws NotEnoughArgumentsException
	{
		final String nick = args[0];
		if (target.getName().equalsIgnoreCase(nick))
		{
			setNickname(server, sender, target, nick);
			target.sendMessage(_("nickNoMore"));
		}
		else if ("off".equalsIgnoreCase(nick))
		{
			setNickname(server, sender, target, null);
			target.sendMessage(_("nickNoMore"));
		}
		else if (nickInUse(server, target, nick))
		{
			throw new NotEnoughArgumentsException(_("nickInUse"));
		}
		else
		{
			setNickname(server, sender, target, nick);
			target.sendMessage(_("nickSet", target.getDisplayName()));
		}
	}

	private String formatNickname(final User user, final String nick) throws Exception
	{
		String newNick = user == null ? FormatUtil.replaceFormat(nick) : FormatUtil.formatString(user, "essentials.nick", nick);
		if (!newNick.matches("^[a-zA-Z_0-9\u00a7]+$"))
		{
			throw new Exception(_("nickNamesAlpha"));
		}
		else if (newNick.length() > ess.getSettings().getMaxNickLength())
		{
			throw new Exception(_("nickTooLong"));
		}
		return newNick;
	}

	private boolean nickInUse(final Server server, final User target, String nick)
	{
		final String lowerNick = nick.toLowerCase(Locale.ENGLISH);
		for (final Player onlinePlayer : server.getOnlinePlayers())
		{
			if (target.getBase() == onlinePlayer)
			{
				continue;
			}
			if (lowerNick.equals(onlinePlayer.getDisplayName().toLowerCase(Locale.ENGLISH))
				|| lowerNick.equals(onlinePlayer.getName().toLowerCase(Locale.ENGLISH)))
			{
				return true;
			}
		}
		return false;
	}

	private void setNickname(final Server server, final CommandSender sender, final User target, final String nickname)
	{
		final User controller = sender instanceof Player ? ess.getUser(sender) : null;
		final NickChangeEvent nickEvent = new NickChangeEvent(controller, target, nickname);
		server.getPluginManager().callEvent(nickEvent);
		if (!nickEvent.isCancelled())
		{
			target.setNickname(nickname);
			target.setDisplayNick();
		}
	}
}
