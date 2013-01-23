package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


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
			if (args[0].contentEquals("*") && user.isAuthorized("essentials.clearinventory.all"))
			{
				if (args.length > 1)
				{
					for (Player onlinePlayer : server.getOnlinePlayers())
					{
						clearInventory(onlinePlayer, args[1]);
					}
					user.sendMessage("Cleared everyone's inventory");
				}
				else
				{
					throw new NotEnoughArgumentsException();
				}
			}
			else if (args[0].length() >= 3)
			{
				List<Player> online = server.matchPlayer(args[0]);

				if (!online.isEmpty())
				{
					for (Player p : online)
					{
						if (args.length > 1)
						{
							clearInventory(p, args[1]);
						}
						else
						{
							p.getInventory().clear();
						}
						user.sendMessage(_("inventoryClearedOthers", p.getDisplayName()));
					}
				}
				else
				{
					clearInventory(user, args[0]);
					user.sendMessage(_("inventoryCleared"));
				}
			}
			else
			{
				Player p = server.getPlayer(args[0]);
				if (p != null)
				{
					clearInventory(p, args[1]);
					user.sendMessage(_("inventoryClearedOthers", p.getDisplayName()));
				}
				else
				{
					clearInventory(user, args[0]);
					user.sendMessage(_("inventoryCleared"));
				}
			}
		}
		else
		{
			if (args.length > 0)
			{
				clearInventory(user, args[0]);
			}
			else
			{
				user.getInventory().clear();
			}
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

		if (args[0].contentEquals("*"))
		{
			if (args.length > 1)
			{
				for (Player onlinePlayer : server.getOnlinePlayers())
				{
					clearInventory(onlinePlayer, args[1]);
				}
				sender.sendMessage("Cleared everyone's inventory");
			}
			else
			{
				throw new NotEnoughArgumentsException();
			}
		}
		else if (args[0].length() >= 3)
		{
			List<Player> online = server.matchPlayer(args[0]);

			if (!online.isEmpty())
			{
				for (Player p : online)
				{
					if (args.length > 1)
					{
						clearInventory(p, args[1]);
					}
					else
					{
						p.getInventory().clear();
					}
					sender.sendMessage(_("inventoryClearedOthers", p.getDisplayName()));
				}
			}
			else
			{
				throw new Exception(_("playerNotFound"));
			}
		}
		else
		{
			Player u = server.getPlayer(args[0]);
			if (u != null)
			{
				clearInventory(u, args[0]);
				sender.sendMessage(_("inventoryClearedOthers", u.getDisplayName()));
			}
			else
			{
				throw new Exception(_("playerNotFound"));
			}
		}
	}

	public void clearInventory(Player player, String arg) throws Exception
	{
		final String[] split = arg.split(":");
		if (arg.contentEquals("*"))
		{
			player.getInventory().clear();
		}
		else
		{
			final ItemStack item = ess.getItemDb().get(split[0]);
			final int type = item.getTypeId();

			if (split.length > 1 && Util.isInt(arg.replace(":", "")))
			{
				player.getInventory().clear(type, Integer.parseInt(split[1]));
			}
			else
			{
				if (Util.isInt(split[0]))
				{
					player.getInventory().clear(type, -1);
				}
				else
				{
					player.getInventory().clear(type, item.getDurability());
				}
			}
		}
	}
}