package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import java.util.List;
import org.bukkit.ChatColor;


public class Commandclearinventory extends EssentialsCommand
{
	public Commandclearinventory()
	{
		super("clearinventory");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0 && user.isAuthorized("essentials.clearinventory.others"))
		{
			if (args[0].length() >= 3)
			{
				List<Player> online = server.matchPlayer(args[0]);

				if (!online.isEmpty())
				{
					for (Player p : online)
					{
						p.getInventory().clear();
						user.sendMessage("§7Inventory of §c" + p.getDisplayName() + "§7 cleared.");
						user.charge(this);
					}
					return;
				}
				throw new Exception("Player not found");
			}
			else
			{
				Player u = server.getPlayer(args[0]);
				if (u != null)
				{
					u.getInventory().clear();
					user.sendMessage("§7Inventory of §c" + u.getDisplayName() + "§7 cleared.");
					user.charge(this);
				}
				else
				{
					throw new Exception("Player not found");
				}
			}
		}
		else
		{
			user.getInventory().clear();
			user.sendMessage("§7Inventory cleared.");
			user.charge(this);
		}
	}

	@Override
	protected void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage(ChatColor.RED + "Usage: " + commandLabel + " [player]");
			return;
		}

		if (args[0].length() >= 3)
		{
			List<Player> online = server.matchPlayer(args[0]);

			if (!online.isEmpty())
			{
				for (Player p : online)
				{
					p.getInventory().clear();
					sender.sendMessage("§7Inventory of §c" + p.getDisplayName() + "§7 cleared.");
				}
				return;
			}
			throw new Exception("Player not found");
		}
		else
		{
			Player u = server.getPlayer(args[0]);
			if (u != null)
			{
				u.getInventory().clear();
				sender.sendMessage("§7Inventory of §c" + u.getDisplayName() + "§7 cleared.");
			}
			else
			{
				throw new Exception("Player not found");
			}
		}
	}
}
