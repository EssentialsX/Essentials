package com.earth2me.essentials.permissions;

import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandmanglistp extends EssentialsCommand
{
	public Commandmanglistp()
	{
		super("manglistp");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final String target = args[0];
		String command = "permissions g:"+target+" perms list";
		sender.sendMessage(commandLabel + " is deprecated. Use " + command + " instead.");
		ess.getServer().dispatchCommand(sender, command);
	}
	
	
}
