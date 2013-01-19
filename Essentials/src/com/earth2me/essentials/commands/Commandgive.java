package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
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

		ItemStack stack = ess.getItemDb().get(args[1]);

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

		try
		{
			if (args.length > 3 && Util.isInt(args[2]) && Util.isInt(args[3]))
			{
				stack.setAmount(Integer.parseInt(args[2]));
				stack.setDurability(Short.parseShort(args[3]));
			}
			else if (args.length > 2 && Integer.parseInt(args[2]) > 0)
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
		}
		catch (NumberFormatException e)
		{
			throw new NotEnoughArgumentsException();
		}

		if (args.length > 3)
		{
			MetaItemStack metaStack = new MetaItemStack(stack);
			boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments();			
			if (allowUnsafe && sender instanceof Player && !ess.getUser(sender).isAuthorized("essentials.enchant.allowunsafe"))
			{
				allowUnsafe = false;
			}
			
			metaStack.parseStringMeta(sender, allowUnsafe, args, Util.isInt(args[3]) ? 4 : 3, ess);
			
			stack = metaStack.getItemStack();
		}

		if (stack.getType() == Material.AIR)
		{
			throw new Exception(_("cantSpawnItem", "Air"));
		}

		final String itemName = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ');
		sender.sendMessage(_("giveSpawn", stack.getAmount(), itemName, giveTo.getDisplayName()));
		if (giveTo.isAuthorized("essentials.oversizedstacks"))
		{
			InventoryWorkaround.addOversizedItems(giveTo.getInventory(), ess.getSettings().getOversizedStackSize(), stack);
		}
		else
		{
			InventoryWorkaround.addItems(giveTo.getInventory(), stack);
		}
		giveTo.updateInventory();
	}
}
