package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
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
		sender.sendMessage(Util.format("balance", Util.formatCurrency(getPlayer(server, args, 0, true).getMoney(), ess)));
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		double bal = (args.length < 1
					  || !(user.isAuthorized("essentials.balance.others")
						   || user.isAuthorized("essentials.balance.other"))
					  ? user
					  : getPlayer(server, args, 0, true)).getMoney();
		user.sendMessage(Util.format("balance", Util.formatCurrency(bal, ess)));
	}
}
