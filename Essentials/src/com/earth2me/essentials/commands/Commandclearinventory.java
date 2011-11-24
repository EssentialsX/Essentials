package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandclearinventory extends EssentialsCommand
{
	public Commandclearinventory()
	{
		super("clearinventory");
	}

	//TODO: Cleanup
	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0 && user.isAuthorized("essentials.clearinventory.others"))
		{
			//TODO: Fix fringe user match case.
			if (args[0].length() >= 3)
			{
				List<Player> online = server.matchPlayer(args[0]);

				if (!online.isEmpty())
				{
					for (Player p : online)
					{
						p.getInventory().clear();
						user.sendMessage(_("inventoryClearedOthers", p.getDisplayName()));
					}
					return;
				}
				throw new Exception(_("playerNotFound"));
			}
			else
			{
				Player p = server.getPlayer(args[0]);
				if (p != null)
				{
					p.getInventory().clear();
					user.sendMessage(_("inventoryClearedOthers", p.getDisplayName()));
				}
				else
				{
					throw new Exception(_("playerNotFound"));
				}
			}
		}
		else
		{
			user.getInventory().clear();
			user.sendMessage(_("inventoryCleared"));
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
					sender.sendMessage(_("inventoryClearedOthers", p.getDisplayName()));
				}
				return;
			}
			throw new Exception(_("playerNotFound"));
		}
		else
		{
			Player u = server.getPlayer(args[0]);
			if (u != null)
			{
				u.getInventory().clear();
				sender.sendMessage(_("inventoryClearedOthers", u.getDisplayName()));
			}
			else
			{
				throw new Exception(_("playerNotFound"));
			}
		}
	}
}
