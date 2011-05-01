package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import org.bukkit.command.CommandSender;


public class Commandbalance extends EssentialsCommand
{
	public Commandbalance()
	{
		super("balance");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		sender.sendMessage("§7Balance: $" + getPlayer(server, args, 0).getMoney());
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		charge(user);
		user.sendMessage("§7Balance: $" + (args.length < 1 || !user.isAuthorized("essentials.balance.other")
										   ? user
										   : getPlayer(server, args, 0)).getMoney());
	}
}
