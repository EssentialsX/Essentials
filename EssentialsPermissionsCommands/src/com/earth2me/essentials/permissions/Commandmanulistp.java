package com.earth2me.essentials.permissions;

import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandmanulistp extends EssentialsCommand
{
	public Commandmanulistp()
	{
		super("manulistp");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final String player = args[0];
		String command = "permissions "+player+" perms list";
		sender.sendMessage(commandLabel + " is deprecated. Use " + command + " instead.");
		ess.getServer().dispatchCommand(sender, command);
	}
	
	
}
