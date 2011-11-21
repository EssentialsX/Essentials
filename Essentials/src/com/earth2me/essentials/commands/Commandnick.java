package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
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
		if (args.length > 1)
		{
			if (!user.isAuthorized("essentials.nick.others"))
			{
				throw new Exception(_("nickOthersPermission"));
			}
			setNickname(server, getPlayer(server, args, 0), args[1]);
			user.sendMessage(_("nickChanged"));
			return;
		}
		setNickname(server, user, args[0]);
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
		setNickname(server, getPlayer(server, args, 0), args[1]);
		sender.sendMessage(_("nickChanged"));
	}

	private void setNickname(final Server server, final User target, final String nick) throws Exception
	{
		if (nick.matches("[^a-zA-Z_0-9]"))
		{
			throw new Exception(_("nickNamesAlpha"));
		}
		else if ("off".equalsIgnoreCase(nick) || target.getName().equalsIgnoreCase(nick))
		{
			target.setNickname(null);
			target.setDisplayNick();
			target.sendMessage(_("nickNoMore"));
		}
		else
		{
			final String formattedNick = nick.replace('&', '\u00a7').replace("\u00a7\u00a7", "&");
			for (Player p : server.getOnlinePlayers())
			{
				if (target.getBase() == p)
				{
					continue;
				}
				String dn = p.getDisplayName().toLowerCase(Locale.ENGLISH);
				String n = p.getName().toLowerCase(Locale.ENGLISH);
				String nk = formattedNick.toLowerCase(Locale.ENGLISH);
				if (nk.equals(dn) || nk.equals(n))
				{
					throw new Exception(_("nickInUse"));
				}
			}

			target.setNickname(formattedNick);
			target.setDisplayNick();
			target.sendMessage(_("nickSet", target.getDisplayName() + "§7."));
		}
	}
}
