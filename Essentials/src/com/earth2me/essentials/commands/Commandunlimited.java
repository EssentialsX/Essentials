package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.InventoryWorkaround;
import com.earth2me.essentials.ItemDb;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.ItemStack;

public class Commandunlimited extends EssentialsCommand
{
	public Commandunlimited()
	{
		super("unlimited");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /" + commandLabel + " [item] <player>");
			return;
		}
		ItemStack stack = ItemDb.get(args[0], 1);

		User target = user;
		
		if (args.length > 1 && user.isAuthorized("essentials.unlimited.others")) {
			target = getPlayer(server, args, 1);
		}		
		
		String itemname = stack.getType().toString().toLowerCase().replace("_", "-");
		if (!user.isAuthorized("essentials.unlimited.item-add") && 
			!user.isAuthorized("essentials.unlimited.item-"+itemname)
			&& !((stack.getType() == Material.WATER_BUCKET || stack.getType() == Material.LAVA_BUCKET)
			&& user.isAuthorized("essentials.unlimited.item-bucket"))) {
			user.sendMessage(ChatColor.RED + "No permission for unlimited item "+itemname+".");
			return;
		}
		
		
		String itemName = stack.getType().name().toLowerCase().replace('_', ' ');
		
		if (target.hasUnlimited(stack)) {
			if (user != target) {
				user.sendMessage("§7Disable unlimited placing of " + itemName + " for " + user.getDisplayName() + ".");
			}
			target.sendMessage("§7Disable unlimited placing of " + itemName + " for " + user.getDisplayName() + ".");
			target.setUnlimited(stack, false);
			return;
		}
		user.charge(this);
		if (user != target) {
			user.sendMessage("§7Giving unlimited amount of " + itemName + " to " + user.getDisplayName() + ".");
		}
		target.sendMessage("§7Giving unlimited amount of " + itemName + " to " + user.getDisplayName() + ".");
		if (!InventoryWorkaround.containsItem((CraftInventory)target.getInventory(), true, stack)) {
			target.getInventory().addItem(stack);
		}
		target.setUnlimited(stack, true);
	}
}

