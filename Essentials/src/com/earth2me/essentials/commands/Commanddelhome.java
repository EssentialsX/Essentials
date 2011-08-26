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
		//Allowing both formats /delhome khobbits house | /delhome khobbits:house
		final String[] nameParts = args[0].split(":");
		if (nameParts[0].length() != args[0].length())
		{
			args = nameParts;
		}

		User user = ess.getUser(sender);
		String name;
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		else if (args.length > 1 && (user == null || user.isAuthorized("essentials.delhome.others")))
		{
			user = getPlayer(server, args, 0);
			name = args[1];
		}
		else
		{
			if (user == null)
			{
				throw new NotEnoughArgumentsException();
			}
			name = args[0];
		}
		user.delHome(name.toLowerCase());
		sender.sendMessage(Util.format("deleteHome", name));
	}
}
