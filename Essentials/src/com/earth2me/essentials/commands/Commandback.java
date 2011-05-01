package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandback extends EssentialsCommand
{
	public Commandback()
	{
		super("back");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		user.canAfford(this);
		user.sendMessage("§7Returning to previous location.");
		user.getTeleport().back(this.getName());
	}
}
