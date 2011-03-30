package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;


public class Commandbalance extends EssentialsCommand
{
	public Commandbalance()
	{
		super("balance");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		user.sendMessage("§7Balance: $" + (args.length < 1 || !user.isAuthorized("essentials.balance.other")
				? user
				: getPlayer(server, args, 0)).getMoney());
	}
}
