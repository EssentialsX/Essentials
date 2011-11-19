package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commanddelhome extends EssentialsCommand
{
	public Commanddelhome()
	{
		super("delhome");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		//Allowing both formats /delhome khobbits house | /delhome khobbits:house
		final String[] expandedArgs = args[0].split(":");

		User user = ess.getUser(sender);
		String name;
		if (expandedArgs.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		else if (expandedArgs.length > 1 && (user == null || user.isAuthorized("essentials.delhome.others")))
		{
			user = getPlayer(server, expandedArgs, 0, true);
			name = expandedArgs[1];
		}
		else
		{
			if (user == null)
			{
				throw new NotEnoughArgumentsException();
			}
			name = expandedArgs[0];
		}
		user.delHome(name.toLowerCase());
		sender.sendMessage(Util.format("deleteHome", name));
	}
}
