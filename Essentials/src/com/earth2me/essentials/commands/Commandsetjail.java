package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandsetjail extends EssentialsCommand
{
	public Commandsetjail()
	{
		super("setjail");
	}
	
	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		charge(user);
		ess.getJail().setJail(user.getLocation(), args[0]);
		user.sendMessage(Util.format("jailSet",args[0]));
		
	}
}
