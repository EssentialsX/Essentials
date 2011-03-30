package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandnuble extends EssentialsCommand
{
	public Commandnuble()
	{
		super("nuble");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		user.charge(this);
		user.sendMessage("§7Flight is allowed on this server.");
	}
}
