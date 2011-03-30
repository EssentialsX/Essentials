package com.earth2me.essentials.commands;

import com.earth2me.essentials.*;
import org.bukkit.*;


public class Commandr extends EssentialsCommand
{
	public Commandr()
	{
		super("r");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /" + commandLabel + " [message]");
			return;
		}

		String message = getFinalArg(args, 0);
		User target = user.getReplyTo();

		if (target == null)
		{
			user.sendMessage("§cYou have nobody to whom you can reply.");
		}

		user.charge(this);
		user.sendMessage("[Me -> " + target.getDisplayName() + "] " + message);
		target.sendMessage("[" + user.getDisplayName() + " -> Me] " + message);
		user.setReplyTo(target);
		target.setReplyTo(user);
	}
}
