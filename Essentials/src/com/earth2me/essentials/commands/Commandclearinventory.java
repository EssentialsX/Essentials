package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.List;
import org.bukkit.ChatColor;


public class Commandclearinventory extends EssentialsCommand
{
	public Commandclearinventory()
	{
		super("clearinventory");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0 && user.isAuthorized("essentials.clearinventory.others"))
		{
			if (args[0].length() >= 3)
			{
				List<Player> online = server.matchPlayer(args[0]);

				if (!online.isEmpty())
				{
					charge(user);
					for (Player p : online)
					{
						p.getInventory().clear();
						user.sendMessage(Util.format("inventoryClearedOthers", p.getDisplayName()));
					}
					return;
				}
				throw new Exception(Util.i18n("playerNotFound"));
			}
			else
			{
				Player p = server.getPlayer(args[0]);
				if (p != null)
				{
					charge(user);
					p.getInventory().clear();
					user.sendMessage(Util.format("inventoryClearedOthers", p.getDisplayName()));
				}
				else
				{
					throw new Exception(Util.i18n("playerNotFound"));
				}
			}
		}
		else
		{
			charge(user);
			user.getInventory().clear();
			user.sendMessage(Util.i18n("inventoryCleared"));
		}
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		if (args[0].length() >= 3)
		{
			List<Player> online = server.matchPlayer(args[0]);

			if (!online.isEmpty())
			{
				for (Player p : online)
				{
					p.getInventory().clear();
					sender.sendMessage(Util.format("inventoryClearedOthers", p.getDisplayName()));
				}
				return;
			}
			throw new Exception(Util.i18n("playerNotFound"));
		}
		else
		{
			Player u = server.getPlayer(args[0]);
			if (u != null)
			{
				u.getInventory().clear();
				sender.sendMessage(Util.format("inventoryClearedOthers", u.getDisplayName()));
			}
			else
			{
				throw new Exception(Util.i18n("playerNotFound"));
			}
		}
	}
}
