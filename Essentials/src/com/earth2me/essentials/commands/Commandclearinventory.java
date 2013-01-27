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
			if (args[0].contentEquals("*") && user.isAuthorized("essentials.clearinventory.all"))
			{
				cleanInventoryAll(server, user, args);
			}
			else if (args[0].trim().length() < 2)
			{
				cleanInventorySelf(server, user, args);
			}
			else
			{
				cleanInventoryOthers(server, user, args);
			}
		}
		else
		{
			cleanInventorySelf(server, user, args);
		}
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0)
		{
			if (args[0].contentEquals("*"))
			{
				cleanInventoryAll(server, sender, args);
			}
			else if (args[0].trim().length() < 2)
			{
				throw new Exception(_("playerNotFound"));
			}
			else
			{
				cleanInventoryOthers(server, sender, args);
			}
		}
		else
		{
			throw new NotEnoughArgumentsException();
		}
	}

	private void cleanInventoryAll(Server server, CommandSender sender, String[] args) throws Exception
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

	private void cleanInventoryOthers(Server server, CommandSender user, String[] args) throws Exception
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
			throw new Exception(_("playerNotFound"));
		}
	}

	private void cleanInventorySelf(Server server, User user, String[] args) throws Exception
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

	private void clearInventory(Player player, String arg) throws Exception
	{
		if (arg.contentEquals("*"))
		{
			player.getInventory().clear();
		}
		else
		{
			final String[] split = arg.split(":");
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