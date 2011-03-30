package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandping extends EssentialsCommand
{
	public Commandping()
	{
		super("ping");
	}

	@Override
	public String[] getTriggers()
	{
		return new String[] { getName(), "pong" };
	}

	@Override
	public void run(Server server, Essentials parent, User player, String commandLabel, String[] args) throws Exception
	{
		player.sendMessage("Pong!");
	}
}
