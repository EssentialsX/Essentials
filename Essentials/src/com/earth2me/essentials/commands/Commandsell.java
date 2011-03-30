package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class Commandsell extends EssentialsCommand
{
	public Commandsell()
	{
		super("sell");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		ItemStack is = user.getInventory().getItemInHand();
		if(is.getType() == Material.AIR)
			throw new Exception("You really tried to sell Air? Put an item in your hand.");

		int id = is.getTypeId();
		int amount = 0;
		if (args.length > 0) amount = Integer.parseInt(args[0].replaceAll("[^0-9]", ""));
		int worth = parent.getConfiguration().getInt("worth-" + id, 0);
		boolean stack = args.length > 0 && args[0].endsWith("s");
		boolean requireStack = parent.getConfiguration().getBoolean("trade-in-stacks-" + id, false);

		if (worth < 1) throw new Exception("That item cannot be sold to the server.");
		if (requireStack && !stack) throw new Exception("Item must be traded in stacks. A quantity of 2s would be two stacks, etc.");

		int max = 0;
		for (ItemStack s :  user.getInventory().all(is).values())
		{
			max += s.getAmount();
		}

		if (stack) amount *= 64;
		if (amount < 1) amount += max;

		if (requireStack)
		{
			amount -= amount % 64;
		}
		
		if (amount > max || amount < 1)
		{
			user.sendMessage("§cYou do not have enough of that item to sell.");
			user.sendMessage("§7If you meant to sell all of your items of that type, use /sell without parameters.");
			user.sendMessage("§7/sell -1 will sell all but one item, etc.");
			return;
		}

		user.charge(this);
		user.getInventory().removeItem(new ItemStack(id, amount));
		user.updateInventory();
		user.giveMoney(worth * amount);
		user.sendMessage("§7Sold for §c$" + (worth * amount) + "§7 (" + amount + " items at $" + worth + " each)");
	}
}
