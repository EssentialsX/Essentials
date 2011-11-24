package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import java.util.Locale;
import org.bukkit.ChatColor;
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

	//TODO: move these messages to message file
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
			throw new Exception(ChatColor.RED + "You are not allowed to spawn the item " + itemname);
		}
		if (args.length > 2 && Integer.parseInt(args[2]) > 0)
		{
			stack.setAmount(Integer.parseInt(args[2]));
		}

		if (stack.getType() == Material.AIR)
		{
			throw new Exception(ChatColor.RED + "You can't give air.");
		}

		final User giveTo = getPlayer(server, args, 0);
		final String itemName = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ');
		sender.sendMessage(ChatColor.BLUE + "Giving " + stack.getAmount() + " of " + itemName + " to " + giveTo.getDisplayName() + ".");
		giveTo.getInventory().addItem(stack);
		giveTo.updateInventory();
	}
}
