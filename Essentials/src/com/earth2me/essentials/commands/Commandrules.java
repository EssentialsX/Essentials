package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.*;


public class Commandrules extends EssentialsCommand
{
	public Commandrules()
	{
		super("rules");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		user.charge(this);
		for (String m : parent.getLines(user, "rules", "§cThere are no rules specified yet."))
		{
			user.sendMessage(m);
		}
	}
}
