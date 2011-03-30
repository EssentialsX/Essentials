package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.command.CommandSender;


public class Commandmotd extends EssentialsCommand
{
	public Commandmotd()
	{
		super("motd");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		User.charge(sender, this);
		for (String m : parent.getMotd(sender, "§cThere is no message of the day."))
		{
			sender.sendMessage(m);
		}
	}
}
