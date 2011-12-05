package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;


public class Commandfind extends EssentialsCommand
{
	public Commandfind()
	{
		super("find");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		ItemStack itemStack = ess.getItemDb().get(args[0]);
		sender.sendMessage(itemStack.getType().toString() + "- " + itemStack.getTypeId() + ":" + Integer.toString(itemStack.getData().getData()));
	}
}
