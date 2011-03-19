package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;


public class Commandpay extends EssentialsCommand
{
	public Commandpay()
	{
		super("pay");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{

		int amount;
		try
		{
			amount = Integer.parseInt(args[1].replaceAll("[^0-9]", ""));
		}
		catch (Exception ex)
		{
			user.sendMessage("§cUsage: /" + commandLabel + " [player] [money]");
			return;
		}

		for (Player p : server.matchPlayer(args[0]))
		{
			User u = User.get(p);
			user.payUser(u, amount);
		}
	}


	
}
