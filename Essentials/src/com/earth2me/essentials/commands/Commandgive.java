package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.User;
import java.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Commandgive extends EssentialsCommand
{
	public Commandgive()
	{
		super("give");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		final ItemStack stack = ess.getItemDb().get(args[1]);

		final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
		if (sender instanceof Player
			&& (ess.getSettings().permissionBasedItemSpawn()
				? (!ess.getUser(sender).isAuthorized("essentials.give.item-all")
				   && !ess.getUser(sender).isAuthorized("essentials.give.item-" + itemname)
				   && !ess.getUser(sender).isAuthorized("essentials.give.item-" + stack.getTypeId()))
				: (!ess.getUser(sender).isAuthorized("essentials.itemspawn.exempt")
				   && !ess.getUser(sender).canSpawnItem(stack.getTypeId()))))
		{
			throw new Exception(_("cantSpawnItem", itemname));
		}

		final User giveTo = getPlayer(server, args, 0);

		if (args.length > 2 && Integer.parseInt(args[2]) > 0)
		{
			stack.setAmount(Integer.parseInt(args[2]));
		}
		else if (ess.getSettings().getDefaultStackSize() > 0)
		{
			stack.setAmount(ess.getSettings().getDefaultStackSize());
		}
		else if (ess.getSettings().getOversizedStackSize() > 0 && giveTo.isAuthorized("essentials.oversizedstacks"))
		{
			stack.setAmount(ess.getSettings().getOversizedStackSize());
		}

		if (args.length > 3)
		{
			for (int i = 3; i < args.length; i++)
			{
				final String[] split = args[i].split("[:+',;.]", 2);
				if (split.length < 1)
				{
					continue;
				}
				final Enchantment enchantment = Commandenchant.getEnchantment(split[0], sender instanceof Player ? ess.getUser(sender) : null);
				int level;
				if (split.length > 1)
				{
					level = Integer.parseInt(split[1]);
				}
				else
				{
					level = enchantment.getMaxLevel();
				}
				stack.addEnchantment(enchantment, level);
			}
		}

		if (stack.getType() == Material.AIR)
		{
			throw new Exception(_("cantSpawnItem", "Air"));
		}

		final String itemName = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ');
		sender.sendMessage(ChatColor.BLUE + "Giving " + stack.getAmount() + " of " + itemName + " to " + giveTo.getDisplayName() + ".");
		if (giveTo.isAuthorized("essentials.oversizedstacks"))
		{
			InventoryWorkaround.addItem(giveTo.getInventory(), true, ess.getSettings().getOversizedStackSize(), stack);
		}
		else
		{
			InventoryWorkaround.addItem(giveTo.getInventory(), true, stack);
		}
		giveTo.updateInventory();
	}
}
