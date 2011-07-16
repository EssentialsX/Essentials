package com.earth2me.essentials.permissions;

import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandmanudelsub extends EssentialsCommand
{
	public Commandmanudelsub()
	{
		super("manudelsub");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		final String player = args[0];
		final String group = args[1];
		String command = "permissions "+player+" parents remove "+group;
		sender.sendMessage(commandLabel + " is deprecated. Use " + command + " instead.");
		ess.getServer().dispatchCommand(sender, command);
	}
	
	
}
