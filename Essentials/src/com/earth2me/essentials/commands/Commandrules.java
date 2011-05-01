package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandrules extends EssentialsCommand
{
	public Commandrules()
	{
		super("rules");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		charge(user);
		for (String m : ess.getLines(user, "rules", "§cThere are no rules specified yet."))
		{
			user.sendMessage(m);
		}
	}
}
