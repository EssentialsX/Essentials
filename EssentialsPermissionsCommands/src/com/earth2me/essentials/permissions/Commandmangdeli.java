package com.earth2me.essentials.permissions;

import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandmangdeli extends EssentialsCommand
{
	public Commandmangdeli()
	{
		super("mangdeli");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		final String target = args[0];
		final String group = args[1];
		String command = "permissions g:"+target+" parents remove "+group;
		sender.sendMessage(commandLabel + " is deprecated. Use " + command + " instead.");
		ess.getServer().dispatchCommand(sender, command);
	}
	
	
}
