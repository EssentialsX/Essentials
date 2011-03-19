package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandreloadall extends EssentialsCommand
{

	public Commandreloadall()
	{
		super("reloadall");
	}

	@Override
	public String[] getTriggers()
	{
		return new String[] { getName(), "rel", "pr" };
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		user.charge(this);
		server.reload();
		user.sendMessage("§7Reloaded all plugins.");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		server.reload();
		sender.sendMessage("Reloaded all plugins.");
	}
}
