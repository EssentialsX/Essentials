package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.ItemDb;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class Commanditem extends EssentialsCommand
{
	public Commanditem()
	{
		super("item");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /" + commandLabel + " [item] <amount>");
			return;
		}
		String[] itemArgs = args[0].split("[^a-zA-Z0-9]");
		ItemStack stack = ItemDb.get(itemArgs[0]);

		if(!user.isAuthorized("essentials.itemspawn.exempt") && !user.canSpawnItem(stack.getTypeId()))
		{
			user.sendMessage(ChatColor.RED + "You are not allowed to spawn that item");
			return;
		}
		if (itemArgs.length > 1) {
			stack.setDurability(Short.parseShort(itemArgs[1]));
		}
		
		if (args.length > 1 && Integer.parseInt(args[1]) > 0) {
			stack.setAmount(Integer.parseInt(args[1]));
		}
		
		if (stack.getType() == Material.AIR) {
			user.sendMessage(ChatColor.RED + "You can't get air.");
			return;
		}

		String itemName = stack.getType().name().toLowerCase().replace('_', ' ');
		user.charge(this);
		user.sendMessage("§7Giving " + stack.getAmount() + " of " + itemName + " to " + user.getDisplayName() + ".");
		user.getInventory().addItem(stack);
	}
}
