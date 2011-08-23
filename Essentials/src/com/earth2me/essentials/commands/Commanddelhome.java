package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Util;


public class Commanddelhome extends EssentialsCommand
{
	public Commanddelhome()
	{
		super("delhome");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		User user;
		String name;
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		else if (args.length < 2)
		{
			user = ess.getUser(sender);
			if (user == null)
			{
				throw new NotEnoughArgumentsException();
			}
			name = args[0];
		}
		else
		{
			user = getPlayer(server, args, 0);
			name = args[1];
		}
		user.delHome(name);
		sender.sendMessage(Util.format("deleteHome", args[0]));
	}
}
