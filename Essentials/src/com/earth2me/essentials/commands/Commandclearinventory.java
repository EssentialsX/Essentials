package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Commandclearinventory extends EssentialsCommand
{
	public Commandclearinventory()
	{
		super("clearinventory");
	}
	private static final int BASE_AMOUNT = 100000;
	private static final int EXTENDED_CAP = 8;

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		parseCommand(server, user.getSource(), args, user.isAuthorized("essentials.clearinventory.others"), user.isAuthorized("essentials.clearinventory.all") || user.isAuthorized("essentials.clearinventory.multiple"));
	}

	@Override
	protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception
	{
		parseCommand(server, sender, args, true, true);
	}

	private void parseCommand(Server server, CommandSource sender, String[] args, boolean allowOthers, boolean allowAll) throws Exception
	{
		List<Player> players = new ArrayList<Player>();
		int offset = 0;

		if (sender.isPlayer())
		{
			players.add(sender.getPlayer());
		}

		if (allowAll && args.length > 0 && args[0].contentEquals("*"))
		{
			sender.sendMessage(tl("inventoryClearingFromAll"));
			offset = 1;
			players = Arrays.asList(server.getOnlinePlayers());
		}
		else if (allowOthers && args.length > 0 && args[0].trim().length() > 2)
		{
			offset = 1;
			players = server.matchPlayer(args[0].trim());
		}

		if (players.size() < 1)
		{
			throw new PlayerNotFoundException();
		}
		for (Player player : players)
		{
			clearHandler(sender, player, args, offset, players.size() < EXTENDED_CAP);
		}
	}

	protected void clearHandler(CommandSource sender, Player player, String[] args, int offset, boolean showExtended) throws Exception
	{
		short data = -1;
		int type = -1;
		int amount = -1;

		if (args.length > (offset + 1) && NumberUtil.isInt(args[(offset + 1)]))
		{
			amount = Integer.parseInt(args[(offset + 1)]);
		}
		if (args.length > offset)
		{
			if (args[offset].equalsIgnoreCase("**"))
			{
				type = -2;
			}
			else if (!args[offset].equalsIgnoreCase("*"))
			{
				final String[] split = args[offset].split(":");
				final ItemStack item = ess.getItemDb().get(split[0]);
				type = item.getTypeId();

				if (split.length > 1 && NumberUtil.isInt(split[1]))
				{
					data = Short.parseShort(split[1]);
				}
				else
				{
					data = item.getDurability();
				}
			}
		}

		if (type == -1) // type -1 represents wildcard or all items
		{
			if (showExtended)
			{
				sender.sendMessage(tl("inventoryClearingAllItems", player.getDisplayName()));
			}
			player.getInventory().clear();
		}
		else if (type == -2) // type -2 represents double wildcard or all items and armor
		{
			if (showExtended)
			{
				sender.sendMessage(tl("inventoryClearingAllArmor", player.getDisplayName()));
			}
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
		}
		else
		{
			if (data == -1) // data -1 means that all subtypes will be cleared
			{
				ItemStack stack = new ItemStack(type);
				if (showExtended)
				{
					sender.sendMessage(tl("inventoryClearingAllStack", stack.getType().toString().toLowerCase(Locale.ENGLISH), player.getDisplayName()));
				}
				player.getInventory().clear(type, data);
			}
			else if (amount == -1) // amount -1 means all items will be cleared
			{
				ItemStack stack = new ItemStack(type, BASE_AMOUNT, data);
				ItemStack removedStack = player.getInventory().removeItem(stack).get(0);
				final int removedAmount = (BASE_AMOUNT - removedStack.getAmount());
				if (removedAmount > 0 || showExtended)
				{
					sender.sendMessage(tl("inventoryClearingStack", removedAmount, stack.getType().toString().toLowerCase(Locale.ENGLISH), player.getDisplayName()));
				}
			}
			else
			{
				if (amount < 0)
				{
					amount = 1;
				}
				ItemStack stack = new ItemStack(type, amount, data);
				if (player.getInventory().containsAtLeast(stack, amount))
				{
					sender.sendMessage(tl("inventoryClearingStack", amount, stack.getType().toString().toLowerCase(Locale.ENGLISH), player.getDisplayName()));
					player.getInventory().removeItem(stack);
				}
				else
				{
					if (showExtended)
					{
						sender.sendMessage(tl("inventoryClearFail", player.getDisplayName(), amount, stack.getType().toString().toLowerCase(Locale.ENGLISH)));
					}
				}
			}
		}
	}
}
