package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandessentials extends EssentialsCommand
{
	public Commandessentials()
	{
		super("essentials");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		parent.reload();
		user.charge(this);
		user.sendMessage("§7Essentials Reloaded " + parent.getDescription().getVersion());
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		parent.reload();
		sender.sendMessage("Essentials Reloaded " + parent.getDescription().getVersion());
	}
}
