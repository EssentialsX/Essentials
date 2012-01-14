package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Commanditemdb extends EssentialsCommand
{
	public Commanditemdb()
	{
		super("itemdb");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		ItemStack itemStack = null;
		if (args.length < 1)
		{
			if (sender instanceof Player)
			{
				itemStack = ((Player)sender).getItemInHand();
			}
			if (itemStack == null)
			{
				throw new NotEnoughArgumentsException();
			}
		}
		else
		{
			itemStack = ess.getItemDb().get(args[0]);
		}
		sender.sendMessage(itemStack.getType().toString() + "- " + itemStack.getTypeId() + ":" + Integer.toString(itemStack.getData().getData()));
	}
}
