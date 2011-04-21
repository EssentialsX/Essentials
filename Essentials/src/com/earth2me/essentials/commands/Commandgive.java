package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.ItemDb;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Commandgive extends EssentialsCommand
{
	public Commandgive()
	{
		super("give");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " [player] [item]<:data> <amount>");
			return;
		}

		String[] itemArgs = args[1].split("[^a-zA-Z0-9]");
		ItemStack stack = ItemDb.get(itemArgs[0]);

		if(sender instanceof Player && !User.get(sender).isAuthorized("essentials.itemspawn.exempt") &&  !User.get(sender).canSpawnItem(stack.getTypeId()))
		{
			sender.sendMessage(ChatColor.RED + "You are not allowed to spawn that item");
			return;
		}
		if (itemArgs.length > 1) {
			stack.setDurability(Short.parseShort(itemArgs[1]));
		}
		if (args.length > 2 && Integer.parseInt(args[2]) > 0) {
			stack.setAmount(Integer.parseInt(args[2]));
		}
		
		if (stack.getType() == Material.AIR) {
			sender.sendMessage(ChatColor.RED + "You can't give air.");
			return;
		}

		User giveTo = getPlayer(server, args, 0);
		String itemName = stack.getType().name().toLowerCase().replace('_', ' ');
		if (sender instanceof Player) {
			User.get(sender).charge(this);
		}
		sender.sendMessage(ChatColor.BLUE + "Giving " + stack.getAmount() + " of " + itemName + " to " + giveTo.getDisplayName() + ".");
		giveTo.getInventory().addItem(stack);
	}
}
