package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import java.util.Locale;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandnick extends EssentialsCommand
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
		if (args.length > 1 && user.isAuthorized("essentials.nick.others"))
		{
			setNickname(server, getPlayer(server, user, args, 0), formatNickname(user, args[1]));
			user.sendMessage(_("nickChanged"));
		}
		else
		{
			setNickname(server, user, formatNickname(user, args[0]));
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
		if ((args[0].equalsIgnoreCase("*") || args[0].equalsIgnoreCase("all")) && args[1].equalsIgnoreCase("off"))
		{
			resetAllNicknames(server);
		}
		else
		{
			setNickname(server, getPlayer(server, args, 0, true, false), formatNickname(null, args[1]));
		}
		sender.sendMessage(_("nickChanged"));
	}

	private String formatNickname(final User user, final String nick)
	{
		if (user == null)
		{
			return FormatUtil.replaceFormat(nick);
		}
		else
		{
			return FormatUtil.formatString(user, "essentials.nick", nick);
		}
	}

	private void resetAllNicknames(final Server server)
	{
		for (Player player : server.getOnlinePlayers())
		{
			try
			{
				setNickname(server, ess.getUser(player), "off");
			}
			catch (Exception ex)
			{
			}
		}
	}

	private void setNickname(final Server server, final User target, final String nick) throws Exception
	{
		if (!nick.matches("^[a-zA-Z_0-9\u00a7]+$"))
		{
			throw new Exception(_("nickNamesAlpha"));
		}
		else if (nick.length() > ess.getSettings().getMaxNickLength())
		{
			throw new Exception(_("nickTooLong"));
		}
		else if (target.getName().equalsIgnoreCase(nick))
		{
			target.setNickname(nick);
			target.setDisplayNick();
			target.sendMessage(_("nickNoMore"));
		}
		else if ("off".equalsIgnoreCase(nick))
		{
			target.setNickname(null);
			target.setDisplayNick();
			target.sendMessage(_("nickNoMore"));
		}
		else
		{
			for (Player onlinePlayer : server.getOnlinePlayers())
			{
				if (target.getBase() == onlinePlayer)
				{
					continue;
				}
				String displayName = onlinePlayer.getDisplayName().toLowerCase(Locale.ENGLISH);
				String name = onlinePlayer.getName().toLowerCase(Locale.ENGLISH);
				String lowerNick = nick.toLowerCase(Locale.ENGLISH);
				if (lowerNick.equals(displayName) || lowerNick.equals(name))
				{
					throw new Exception(_("nickInUse"));
				}
			}

			target.setNickname(nick);
			target.setDisplayNick();
			target.sendMessage(_("nickSet", target.getDisplayName() + "§7."));
		}
	}
}
