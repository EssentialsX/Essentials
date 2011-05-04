package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.text.DecimalFormat;
import org.bukkit.command.CommandSender;


public class Commandbalance extends EssentialsCommand
{
	private static DecimalFormat df = new DecimalFormat("0.##");
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
		sender.sendMessage("§7Balance: " + Util.formatCurrency(getPlayer(server, args, 0).getMoney()));
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		charge(user);
		double bal = (args.length < 1 || !user.isAuthorized("essentials.balance.other")
										   ? user
										   : getPlayer(server, args, 0)).getMoney();
		user.sendMessage("§7Balance: " + Util.formatCurrency(bal));
	}
}
