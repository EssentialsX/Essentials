package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


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
			throw new Exception(Util.i18n("nickDisplayName"));
		}
		if (args.length > 1)
		{
			if (!user.isAuthorized("essentials.nick.others"))
			{
				throw new Exception(Util.i18n("nickOthersPermission"));
			}
			setNickname(server, getPlayer(server, args, 0), args[1]);
			user.sendMessage(Util.i18n("nickChanged"));
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
			throw new Exception(Util.i18n("nickDisplayName"));
		}
		setNickname(server, getPlayer(server, args, 0), args[1]);
		sender.sendMessage(Util.i18n("nickChanged"));
	}

	private void setNickname(final Server server, final User target, final String nick) throws Exception
	{
		if (nick.matches("[^a-zA-Z_0-9]"))
		{
			throw new Exception(Util.i18n("nickNamesAlpha"));
		}
		else if ("off".equalsIgnoreCase(nick) || target.getName().equalsIgnoreCase(nick))
		{
			target.setDisplayNick(target.getName());
			target.setNickname(null);
			target.sendMessage(Util.i18n("nickNoMore"));
		}
		else
		{
			final String formattedNick = nick.replace('&', '§').replace('§§', '&');
			for (Player p : server.getOnlinePlayers())
			{
				if (target.getBase() == p)
				{
					continue;
				}
				String dn = p.getDisplayName().toLowerCase();
				String n = p.getName().toLowerCase();
				String nk = formattedNick.toLowerCase();
				if (nk.equals(dn) || nk.equals(n))
				{
					throw new Exception(Util.i18n("nickInUse"));
				}
			}

			target.setDisplayNick(ess.getSettings().getNicknamePrefix() + formattedNick);
			target.setNickname(formattedNick);
			target.sendMessage(Util.format("nickSet", target.getDisplayName() + "§7."));
		}
	}
}
