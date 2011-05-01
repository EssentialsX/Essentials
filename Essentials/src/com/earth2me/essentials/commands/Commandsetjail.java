package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


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
		Essentials.getJail().setJail(user.getLocation(), args[0]);
		user.sendMessage("§7Jail " + args[0] + " has been set");
		
	}
}
